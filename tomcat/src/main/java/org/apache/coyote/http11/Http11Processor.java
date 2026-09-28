package org.apache.coyote.http11;

import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
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

            final var request = HttpRequest.read(inputStream);
            final var response = new HttpResponse(outputStream);
            final var previousSession = request.getSession(false);

            final var controller = requestMapping.getController(request);
            controller.service(request, response);
            addSessionCookie(request, response, previousSession);
            response.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookie(final HttpRequest request, final HttpResponse response, final Session previousSession) {
        final var session = request.getSession(true);
        if (previousSession == null || !previousSession.getId().equals(session.getId())) {
            response.addHeader("Set-Cookie", HttpCookie.ofJSessionId(session.getId()));
        }
    }
}
