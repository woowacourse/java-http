package org.apache.coyote.http11;


import com.techcourse.RequestMapping;
import com.techcourse.controller.Controller;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.resource.StaticResourceLoader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;

    private final SessionManager sessionManager = new SessionManager();
    private final StaticResourceLoader staticResourceLoader = new StaticResourceLoader();
    private final RequestMapping requestMapping = new RequestMapping(sessionManager, staticResourceLoader);

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

            HttpRequest request = HttpRequest.parse(inputStream);
            HttpResponse response = new HttpResponse();

            Controller controller = requestMapping.getController(request);

            controller.service(request, response);

            addSessionCookieIfNeeded(request, response);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addSessionCookieIfNeeded(HttpRequest request, HttpResponse response) {

        // 이미 JSESSIONID 있으면 추가 발급 x
        if (sessionManager.hasSessionId(request.getHeaders())) {
            return;
        }

        // 이미 위에서 세션을 응답에 넣어줄 경우 발급 X
        if (response.hasHeader("Set-Cookie")) {
            return;
        }

        Session session = sessionManager.getSession(request.getHeaders(), true);
        HttpCookie cookie = new HttpCookie(session.getId());

        response.addHeader(
                "Set-Cookie",
                List.of(cookie.toString())
        );
    }
}
