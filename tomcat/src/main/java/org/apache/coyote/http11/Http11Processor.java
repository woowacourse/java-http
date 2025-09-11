package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.mapping.Controller;
import org.apache.catalina.mapping.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.util.StaticResourceHandler;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.request.HttpRequestParser;
import org.apache.coyote.util.response.HttpContentTypeResolver;
import org.apache.coyote.util.response.HttpResponse;
import org.apache.coyote.util.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            HttpRequest request = HttpRequestParser.parse(inputStream);
            if (request == null) {
                createNotFoundResponse().send(outputStream);
                return;
            }
            Controller controller = RequestMapping.getController(request.getPath());
            if (controller != null) {
                HttpResponse response = new HttpResponse();
                controller.service(request, response);
                response.send(outputStream);
                return;
            }
            HttpResponse response = new HttpResponse();
            if (handleStaticResource(request, response)) {
                response.send(outputStream);
                return;
            }
            createNotFoundResponse().send(outputStream);
        } catch (Exception e) {
            log.error("process error: {}", e.getMessage(), e);
            handleInternalServerError(connection);
        }
    }

    private boolean handleStaticResource(HttpRequest request, HttpResponse response) {
        String resourcePath = "static" + request.getPath();
        byte[] body = StaticResourceHandler.readResource(resourcePath);
        if (body == null) {
            return false;
        }
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", HttpContentTypeResolver.resolve(request.getPath()));
        response.setBody(body);
        return true;
    }

    private void handleInternalServerError(Socket connection) {
        if (connection.isClosed()) {
            return;
        }
        try {
            createInternalServerErrorResponse().send(connection.getOutputStream());
        } catch (IOException e) {
            log.error("handleInternalServerError send error: {}", e.getMessage(), e);
        }
    }

    private HttpResponse createNotFoundResponse() {
        return createErrorResponse(HttpStatus.NOT_FOUND);
    }

    private HttpResponse createInternalServerErrorResponse() {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpResponse createErrorResponse(HttpStatus status) {
        HttpResponse response = new HttpResponse();
        response.setStatus(status);
        String resourcePath = "static/" + status.getCode() + ".html";
        byte[] body = StaticResourceHandler.readResource(resourcePath);
        if (body != null) {
            response.setBody(body);
            response.addHeader("Content-Type", "text/html;charset=utf-8");
        }
        return response;
    }
}
