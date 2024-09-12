package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Cookie;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestControllerMapper;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Http11Request request = Http11Request.from(inputStream);
            Http11Response response = new Http11Response(HttpStatusCode.OK, "", "");
            User user = checkUser(request);

            // 여기부터 response 만들기
            if (request.isStaticRequest()) {
                response = getStaticResponse(request);
            }

            if (!request.isStaticRequest()) {
                Controller controller = RequestControllerMapper.getController(request.getUri());
                if (controller == null) {
                    return; // 404 Not Found
                }
                try {
                    controller.service(request, response);
                } catch (Exception e) {
                    return; // error while service
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private User checkUser(Http11Request request) {
        User user = null;
        List<Cookie> cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), "jsessionid")) {
                Session session = SessionManager.findSession(cookie.getValue());
                if (session == null) return null;
                user = session.getUser();
            }
        }
        return user;
    }

    private Http11Response getStaticResponse(Http11Request request) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader.getResourceAsStream("static/" + request.getUri())) {
            if (stream == null) {
                return new Http11Response(HttpStatusCode.OK, "", "");
            }
            String responseBody = new String(stream.readAllBytes());
            return new Http11Response(HttpStatusCode.OK, responseBody, getExtension(request));
        }
    }

    private static String getExtension(Http11Request request) {
        if (!request.isStaticRequest()) {
            return null;
        }

        int index = request.getUri().lastIndexOf(".");
        String fileExtension = request.getUri().substring(index + 1);
        if (fileExtension.equals("js")) {
            fileExtension = "javascript";
        }
        return fileExtension;
    }
}
