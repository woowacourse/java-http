package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER =
            SessionManager.getInstance();

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestMapping(new StaticResourceController()));
    }

    public Http11Processor(
            final Socket connection,
            final RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}",
                connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpResponse response = new HttpResponse(outputStream);
            final HttpRequest request;

            try {
                request = HttpRequest.read(inputStream);
            } catch (IllegalArgumentException e) {
                response.setStatus(400, "Bad Request");
                response.write();
                return;
            }

            if (request == null) {
                return;
            }

            attachSession(request, response);

            try {
                requestMapping.getController(request).service(request, response);
            } catch (IllegalArgumentException e) {
                response.setStatus(400, "Bad Request");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                response.setStatus(500, "Internal Server Error");
            }

            response.write();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void attachSession(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final String sessionId =
                new HttpCookie(request.getHeader("Cookie")).get("JSESSIONID");

        if (sessionId == null || sessionId.isBlank()) {
            request.getOrCreateSession(response);
            return;
        }

        final Session session = SESSION_MANAGER.findSession(sessionId);
        request.setSession(session);
    }
}
