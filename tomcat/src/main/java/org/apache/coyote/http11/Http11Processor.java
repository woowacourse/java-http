package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Formatter;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
            performProcess(inputStream, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void performProcess(final InputStream inputStream, final OutputStream outputStream) throws Exception {
        try {
            HttpRequest httpRequest = new HttpRequest(inputStream);
            RequestMapping requestMapping = RequestMapping.getInstance();
            HttpResponse response = requestMapping.dispatch(httpRequest);

            outputStream.write(Formatter.toResponseFormat(response));
            outputStream.flush();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            sendErrorResponse(HttpStatus.BAD_REQUEST);
        }
    }

    private void sendErrorResponse(HttpStatus httpStatus) {
        try (var outputStream = connection.getOutputStream()) {
            HttpResponse errorResponse = new HttpResponse(httpStatus, new ResponseHeader(), null);
            errorResponse.redirectTo("/" + httpStatus.getCode() + ".html");

            outputStream.write(Formatter.toResponseFormat(errorResponse));
            outputStream.flush();
        } catch (RuntimeException | IOException e) {
            log.error("에러 핸들링 실패: {}", e.getMessage(), e);
        }
    }
}
