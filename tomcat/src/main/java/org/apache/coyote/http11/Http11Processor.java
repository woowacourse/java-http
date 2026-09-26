package org.apache.coyote.http11;

import org.apache.catalina.Manager;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.HttpParseException;
import org.apache.coyote.http11.request.Http11RequestProcessor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager manager;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, RequestMapping requestMapping, final Manager manager) {
        this.connection = connection;
        this.manager = manager;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpResponse httpResponse = new HttpResponse();

            try {
                final HttpRequest httpRequest = new Http11RequestProcessor(inputStream, manager).process();
                handleEndpoints(httpRequest, httpResponse);
                attachSessionCookie(httpRequest, httpResponse);
            } catch (HttpParseException | URISyntaxException e) {
                log.warn("잘못된 HTTP 요청입니다.");
                httpResponse.setError(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                httpResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            outputStream.write(httpResponse.toHttpBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleEndpoints(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        requestMapping.getController(httpRequest).service(httpRequest, httpResponse);
    }

    private void attachSessionCookie(HttpRequest request, HttpResponse response) {
        request.createdSession().ifPresent(
                session -> response.addCookie(new Cookie(HttpRequest.SESSION_ID, session.getId()))
        );
    }
}
