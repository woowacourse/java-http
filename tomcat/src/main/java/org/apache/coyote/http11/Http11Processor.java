package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.mapper.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final Manager sessionManager;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping, final Manager sessionManager) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = HttpRequest.readFrom(inputStream, sessionManager);
            final HttpResponse response = new HttpResponse();
            final Controller controller = requestMapping.getController(request);
            controller.handle(request, response);
            addSessionCookieIfIssued(request, response);
            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookieIfIssued(final HttpRequest request, final HttpResponse response)
            throws IOException {
        final Session session = request.getSession(false);
        if (session == null) {
            return;
        }
        final boolean clientHasSameSessionId = request.requestedSessionId()
                .filter(session.getId()::equals)
                .isPresent();
        if (!clientHasSameSessionId) {
            response.addCookie(HttpCookie.JSESSIONID, session.getId());
        }
    }
}
