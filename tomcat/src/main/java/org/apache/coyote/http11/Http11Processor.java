package org.apache.coyote.http11;

import org.apache.catalina.Controller;
import org.apache.catalina.ControllerMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerMapping controllerMapping;

    public Http11Processor(final Socket connection, final ControllerMapping controllerMapping) {
        this.connection = Objects.requireNonNull(connection);
        this.controllerMapping = Objects.requireNonNull(controllerMapping);
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = connection.getOutputStream()) {

            HttpResponse response = handle(inputStream);
            response.writeTo(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(InputStream inputStream) {
        try {
            HttpRequest request = HttpRequest.parse(inputStream);
            HttpResponse response = new HttpResponse();

            request.createJSessionIdIfAbsent()
                    .ifPresent(sessionId -> response.setCookie(HttpCookie.JSESSION_ID, sessionId));

            Controller controller = controllerMapping.getController(request);
            controller.service(request, response);
            return response;
        } catch (UnsupportedHttpMethodException e) {
            log.warn(e.getMessage());
            return errorResponse(HttpStatus.NOT_IMPLEMENTED);
        } catch (HttpRequestParseException e) {
            log.warn(e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("요청 처리 중 오류가 발생했습니다.", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse errorResponse(HttpStatus status) {
        HttpResponse response = new HttpResponse();
        response.sendError(status, status.getMessage());
        return response;
    }
}
