package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

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

            final var request = new HttpRequest(inputStream);
            final var response = new HttpResponse(outputStream);
            final var session = request.getSession(true);
            final var sessionCookie = createSessionCookie(request.getRequestedSessionId(), session);
            if (sessionCookie != null) {
                response.addCookie(sessionCookie);
            }

            REQUEST_MAPPING.getController(request).service(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createSessionCookie(final String requestedSessionId, final Session session) {
        if (session.getId().equals(requestedSessionId)) {
            return null;
        }

        return "JSESSIONID=" + session.getId();
    }
}
