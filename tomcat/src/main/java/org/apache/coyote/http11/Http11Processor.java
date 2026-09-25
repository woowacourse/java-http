package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String BAD_REQUEST_MESSAGE = "잘못된 요청입니다.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버에서 오류가 발생했습니다.";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpResponse response = new HttpResponse();
            try {
                final HttpRequest request = HttpRequest.from(reader);
                if (request == null) {
                    return;
                }
                log.info("{} {}", request.getMethod(), request.getPath());
                final Controller controller = requestMapping.getController(request);
                controller.service(request, response);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 요청입니다. {}", e.getMessage());
                setErrorResponse(response, HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
            }

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setErrorResponse(final HttpResponse response, final HttpStatus status, final String message) {
        response.setStatus(status);
        response.setContentType(ContentType.HTML);
        response.setBody(message);
    }
}
