package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.core.StaticResourceHandler;
import org.apache.catalina.mapping.Controller;
import org.apache.catalina.mapping.RequestMapping;
import org.apache.coyote.Processor;
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
            HttpResponse response = new HttpResponse();

            if (request == null) {
                handleError(outputStream, response, HttpStatus.NOT_FOUND);
                return;
            }

            Controller controller = RequestMapping.getController(request.getPath());
            if (controller != null) {
                controller.service(request, response);
                response.send(outputStream);
                return;
            }

            if (handleStaticResource(request, response)) {
                response.send(outputStream);
                return;
            }

            handleError(outputStream, response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("process error: {}", e.getMessage(), e);
            handleError(connection, new HttpResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    private void handleError(OutputStream outputStream, HttpResponse response, HttpStatus status) {
        response.setStatus(status);
        byte[] body = StaticResourceHandler.readResource("static/" + status.getCode() + ".html");
        if (body != null) {
            response.setBody(body);
            response.addHeader("Content-Type", "text/html;charset=utf-8");
        }
        try {
            response.send(outputStream);
        } catch (IOException e) {
            log.error("handleError send error: {}", e.getMessage(), e);
        }
    }

    private void handleError(Socket connection, HttpResponse response, HttpStatus status) {
        if (connection.isClosed()) {
            return;
        }
        try {
            handleError(connection.getOutputStream(), response, status);
        } catch (IOException e) {
            log.error("handleError connection error: {}", e.getMessage(), e);
        }
    }
}
