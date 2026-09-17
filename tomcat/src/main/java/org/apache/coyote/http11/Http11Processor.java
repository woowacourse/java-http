package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";
    private static final String DEFAULT_RESOURCE_PATH = "static/index.html";

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final var requestLine = reader.readLine();
            final var resourcePath = extractResourcePath(requestLine);

            if (resourcePath == null) {
                writeResource(outputStream, DEFAULT_RESOURCE_PATH, "200 OK");
                return;
            }
            writeResource(outputStream, resourcePath, "200 OK");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractResourcePath(final String requestLine) {
        if (requestLine == null) {
            return null;
        }

        final var requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            return null;
        }

        final var requestPath = requestParts[1];
        if ("/".equals(requestPath)) {
            return null;
        }

        return STATIC_RESOURCE_PATH + requestPath;
    }

    private void writeResource(
            final OutputStream outputStream,
            final String resourcePath,
            final String status
    ) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                writeNotFoundResponse(outputStream);
                return;
            }

            final var contentType = MimeTypeResolver.resolve(resourcePath);
            writeResponse(outputStream, contentType, status, resource.readAllBytes());
        }
    }

    private void writeNotFoundResponse(final OutputStream outputStream) throws IOException {
        try (InputStream resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(NOT_FOUND_RESOURCE_PATH),
                "404 페이지를 찾을 수 없습니다."
        )) {
            writeResponse(outputStream, "text/html", "404 Not Found", resource.readAllBytes());
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String contentType,
            final String status,
            final byte[] responseBody
    ) throws IOException {
        final var contentTypeHeader = addCharsetIfNecessary(contentType);
        final var responseHeader = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentTypeHeader + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");

        outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private String addCharsetIfNecessary(final String contentType) {
        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }
        return contentType;
    }
}
