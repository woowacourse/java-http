package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final ControllerResolver mapping;

    public Http11Processor(Socket connection, ControllerResolver mapping) {
        this.connection = connection;
        this.mapping = mapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpResponse response = readResponse(inputStream);
            response.writeTo(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse readResponse(InputStream inputStream) throws IOException {
        HttpRequest request;
        try {
            request = new HttpRequestParser(inputStream).parse();
        } catch (IllegalArgumentException e) {
            return error(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpResponse response = createResponseSafely(request);
        request.getNewSession().ifPresent(session -> addSessionCookie(response, session));
        return response;
    }

    private HttpResponse createResponseSafely(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        try {
            dispatch(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private HttpResponse error(HttpStatus status) {
        HttpResponse response = new HttpResponse();
        response.sendError(status);
        return response;
    }

    private void addSessionCookie(HttpResponse response, Session session) {
        HttpCookie sessionCookie = new HttpCookie(
                HttpCookie.SESSION_COOKIE_KEY,
                session.getId()
        );
        response.addHeader("Set-Cookie", sessionCookie.toHeaderValue());
    }

    private void dispatch(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Controller> controller = mapping.getController(request);
        if (controller.isPresent()) {
            controller.get().service(request, response);
            return;
        }
        serveStaticResource(request, response);
    }

    private void serveStaticResource(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod() != HttpMethod.GET) {
            response.sendError(HttpStatus.NOT_FOUND);
            return;
        }
        response.sendStaticFile(request.getPath());
    }
}
