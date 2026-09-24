package org.apache.coyote.http11;

import com.techcourse.controller.FrontController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController = new FrontController();

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
            final var request = new HttpRequest(inputStream);
            final var response = new HttpResponse();
            final var sessionIdToSet = prepareSession(request);

            final var controller = frontController.handle(request);
            controller.service(request, response);

            if (sessionIdToSet != null) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + sessionIdToSet);
            }

            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String prepareSession(final HttpRequest request) throws IOException {
        final var cookie = HttpCookie.parse(request.getHeader("cookie"));
        final var sessionId = cookie.getValue("JSESSIONID");
        Session session = null;

        if (sessionId.isPresent()) {
            session = SessionManager.getInstance().findSession(sessionId.get());
        }

        if (session == null) {
            final var newSessionId = UUID.randomUUID().toString();
            session = new Session(newSessionId);
            SessionManager.getInstance().add(session);
            request.setSession(session);
            return newSessionId;
        }

        request.setSession(session);
        return null;
    }
}
