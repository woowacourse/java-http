package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.controller.FrontController;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final FrontController frontController;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.frontController = new FrontController();
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
            HttpResponse response = new HttpResponse(outputStream);
            try {
                HttpRequest request = new HttpRequest(inputStream);
                frontController.service(request, response);
            } catch (BadRequestException e) {
                response.send(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                log.error("Internal server error", e);
                response.send(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
