package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Cookie;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.Http11ResponseHeaders;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
            User user = checkUser(request);

            // 여기부터 response 만들기
            Http11Response response = null;
            if (request.isStaticRequest()) {
                response = getStaticResponse(request);
            }

            if (!request.isStaticRequest()) {
                if (request.getUri().startsWith("/login")) {
                    response = login(request);
                }
                if (request.getUri().startsWith("/register")) {
                    response = register(request);
                }
            }

            if (response == null) {
                return;
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

    private Http11Response register(Http11Request request) {
        Http11RequestBody requestBody = request.getRequestBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        return new Http11Response(HttpStatusCode.FOUND, "",
                Http11ResponseHeaders.builder().
                        addHeader("Location", "/index.html")
                        .build());
    }

    private Http11Response login(Http11Request request) {
        Map<String, String> queryParameters = request.getQueryParameters();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryParameters.get("account"));
        HttpStatusCode statusCode = HttpStatusCode.FOUND;
        String redirectUri = "/401.html";

        Http11Response response = new Http11Response(statusCode, "", Http11ResponseHeaders.instance());

        User user;
        if (optionalUser.isPresent() && (user = optionalUser.get()).checkPassword(queryParameters.get("password"))) {
            Session session = Session.getInstance(user);
            SessionManager.add(session);
            response.addHeader("Set-Cookie", " JSESSIONID=" + session.getId());
            redirectUri = "/index.html";
        }

        response.addHeader("Location", " " + redirectUri);
        return response;
    }

    private Http11Response getStaticResponse(Http11Request request) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader.getResourceAsStream("static/" + request.getUri())) {
            assert stream != null;
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
