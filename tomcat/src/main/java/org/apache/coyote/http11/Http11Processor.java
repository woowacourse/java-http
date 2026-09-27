package org.apache.coyote.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final SessionManager sessionManager,
                           final RequestMapping requestMapping) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (connection;
             final var input = new BufferedInputStream(connection.getInputStream());
             final var output = connection.getOutputStream()) {
            final Optional<HttpResponse> response = handle(input);
            if (response.isPresent()) {
                response.get().writeTo(output);
            }
        } catch (IOException e) {
            log.error("Failed to read or write HTTP connection", e);
        }
    }

    private Optional<HttpResponse> handle(final InputStream input) {
        final Optional<HttpRequest> parsed;
        try {
            parsed = HttpRequest.read(input);
        } catch (IOException e) {
            return Optional.of(HttpResponse.error(400, "Bad Request"));
        }
        return parsed.map(this::service);
    }

    private HttpResponse service(final HttpRequest request) {
        request.initializeSession(sessionManager);
        HttpResponse response = new HttpResponse();
        try {
            requestMapping.getController(request).service(request, response);
        } catch (Exception e) {
            log.error("Failed to handle HTTP request", e);
            response = HttpResponse.error(500, "Internal Server Error");
        }
        if (request.getCookie(HttpCookie.JSESSIONID).isEmpty()) {
            response.addCookie(HttpCookie.JSESSIONID, request.getSession().getId());
        }
        return response;
    }
}
