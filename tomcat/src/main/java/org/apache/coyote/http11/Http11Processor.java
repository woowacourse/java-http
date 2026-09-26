package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = new RequestMapping();

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
            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            HttpResponse response = handleRequest(httpRequest);

            outputStream.write(response.toHttpMessage().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) throws Exception {
        String requestTarget = httpRequest.getRequestTarget();

        if (requestTarget.equals("/")) {
            return HttpResponse.create("200 OK", "text/html", "Hello world!");
        }

        if (httpRequest.isPath("/login") || httpRequest.isPath("/register")) {
            Controller controller = requestMapping.getController(requestTarget);
            return controller.service(httpRequest);
        }

        String resourcePath = "static" + requestTarget;
        String contentType = getContentType(requestTarget);

        if (ClassLoader.getSystemResource(resourcePath) == null) {
            return HttpResponse.create("404 Not Found", "text/html", createResponseBody("static/404.html"));
        }

        return HttpResponse.create("200 OK", contentType, createResponseBody(resourcePath));
    }

    private String getContentType(String requestTarget) {
        if (requestTarget.endsWith(".css")) {
            return "text/css";
        }
        if (requestTarget.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    private String createResponseBody(String resourcePath) throws URISyntaxException, IOException {
        final Path path = Path.of(ClassLoader.getSystemResource(resourcePath).toURI());
        return Files.readString(path);
    }
}
