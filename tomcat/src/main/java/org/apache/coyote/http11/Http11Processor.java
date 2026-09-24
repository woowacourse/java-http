package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = RequestMapping.createDefault();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info(
                "connect host: {}, port: {}",
                connection.getInetAddress(),
                connection.getPort()
        );

        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.from(inputStream);

            if (request == null) {
                return;
            }

            HttpResponse response = new HttpResponse(outputStream);
            prepareSession(request, response);

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void prepareSession(HttpRequest request, HttpResponse response) {
        String sessionId = findOrCreateSessionId(request);
        request.setSessionId(sessionId);

        if (request.getCookie(Cookie.JSESSIONID)
                .map(Cookie::getValue)
                .filter(sessionId::equals)
                .isEmpty()) {
            response.addCookie(Cookie.createJSessionId(sessionId));
        }
    }

    private String findOrCreateSessionId(HttpRequest request) {
        return request.getCookie(Cookie.JSESSIONID)
                .map(Cookie::getValue)
                .filter(sessionId -> SessionManager.findSession(sessionId) != null)
                .orElseGet(() -> {
                    Session session = SessionManager.create();
                    return session.getId();
                });
    }
}
