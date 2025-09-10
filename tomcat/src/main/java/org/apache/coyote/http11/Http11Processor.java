package org.apache.coyote.http11;

import com.techcourse.WebApplicationInitializer;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.handler.StaticResourceHandler;
import org.apache.catalina.session.SimpleHttpSession;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = WebApplicationInitializer.getRequestMapping();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var outputStream = connection.getOutputStream();
            HttpResponse response;

            try {
                final var request = HttpRequest.from(reader);
                final var session = request.getSession(true);

                final var controller = requestMapping.getController(request.getPath());
                response = (controller != null)
                        ? controller.service(request)
                        : handleStaticResource(request);

                if (session instanceof SimpleHttpSession s && s.isNew()) {
                    response = response.toBuilder()
                            .setCookie("JSESSIONID", s.getId())
                            .build();
                }
            } catch (Exception e) {
                log.error("Internal Server Error: {}", e.getMessage(), e);
                response = buildInternalServerErrorResponse();
            }
            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error("Fatal error while processing connection", e);
        }
    }

    private HttpResponse handleStaticResource(final HttpRequest request) throws Exception {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return HttpResponse.builder()
                    .status(405, "Method Not Allowed")
                    .contentType("text/plain;charset=utf-8")
                    .body("Method Not Allowed".getBytes())
                    .build();
        }

        return StaticResourceHandler.serveStaticResource(request.getPath());
    }

    private HttpResponse buildInternalServerErrorResponse() {
        try {
            final var body = readStaticFile("static/500.html");

            return HttpResponse.builder()
                    .status(500, "Internal Server Error")
                    .contentType("text/html;charset=utf-8")
                    .body(body)
                    .build();
        } catch (Exception ex) {
            return HttpResponse.builder()
                    .status(500, "Internal Server Error")
                    .contentType("text/plain;charset=utf-8")
                    .body("Internal Server Error".getBytes())
                    .build();
        }
    }

    private byte[] readStaticFile(final String classpathLocation) throws Exception {
        final var resourceUrl = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource(classpathLocation),
                "Resource not found: " + classpathLocation
        );
        final var resourceUri = resourceUrl.toURI();

        return Files.readAllBytes(Paths.get(resourceUri));
    }
}
