package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(
                connection,
                new RequestMapping()
        );
    }

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                    )
            );

            final HttpRequest request = new HttpRequest(reader);
            final HttpResponse response = new HttpResponse();

            applySessionCookie(request, response);

            service(request, response);

            final String httpResponse = response.toResponse();

            outputStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));

            outputStream.flush();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        final Optional<Controller> controller =
                requestMapping.getController(request);

        if (controller.isEmpty()) {
            serveStaticResource(
                    request.getPath(),
                    response
            );
            return;
        }

        controller.get().service(request, response);

        if (response.hasResourcePath()) {
            serveStaticResource(
                    response.getResourcePath(),
                    response
            );
        }
    }

    private void applySessionCookie(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Session session = request.getSession();

        if (!request.isNewSession()) {
            return;
        }

        response.addHeader(
                "Set-Cookie",
                "JSESSIONID=" + session.getId()
        );
    }

    private void serveStaticResource(
            final String path,
            final HttpResponse response
    ) throws IOException, URISyntaxException {
        if ("/".equals(path)) {
            response.addHeader(
                    "Content-Type",
                    "text/html;charset=utf-8"
            );

            response.setBody("Hello world!");
            return;
        }

        final String resourcePath =
                resolveResourcePath(path);

        final URL resource = getClass()
                .getClassLoader()
                .getResource(
                        "static" + resourcePath
                );

        if (resource == null) {
            response.setStatus(
                    404,
                    "Not Found"
            );

            response.setBody("");
            return;
        }

        final URI fileUri = resource.toURI();
        final Path filePath = Paths.get(fileUri);

        final byte[] fileBytes =
                Files.readAllBytes(filePath);

        response.addHeader(
                "Content-Type",
                resolveContentType(resourcePath)
        );

        response.setBody(
                new String(
                        fileBytes,
                        StandardCharsets.UTF_8
                )
        );
    }

    private String resolveResourcePath(
            final String path
    ) {
        if (hasExtension(path)) {
            return path;
        }

        return path + ".html";
    }

    private boolean hasExtension(
            final String path
    ) {
        final int slashIndex =
                path.lastIndexOf('/');

        final int dotIndex =
                path.lastIndexOf('.');

        return dotIndex > slashIndex;
    }

    private String resolveContentType(
            final String path
    ) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "text/javascript";
        }

        return "text/html;charset=utf-8";
    }
}