package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            final String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final String contentType = getContentType(requestLine);
            final byte[] responseBody = getResponseBody(requestLine);
            final String responseHeader = getResponseHeader(contentType, responseBody.length);

            final var response = String.join("\r\n",
                responseHeader, new String(responseBody, StandardCharsets.UTF_8)
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String requestLine) {
        String requestTarget = requestLine.split(" ")[1];
        if (requestTarget.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }
        if (requestTarget.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestTarget.endsWith(".js")) {
            return "application/javascript ";
        }
        if (requestTarget.endsWith(".ico")) {
            return "image/x-icon ";
        }
        return "text/html;charset=utf-8 ";
    }

    private String getResponseHeader(final String contentType, final int contentLength) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType,
            "Content-Length: " + contentLength + " ",
            ""
        );
    }

    private byte[] getResponseBody(final String requestLine) throws URISyntaxException, IOException {
        final String requestTarget = requestLine.split(" ")[1];
        if ("/".equals(requestTarget)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        final Path resourcePath = Path.of(Objects.requireNonNull(
            getClass().getClassLoader()
                .getResource(String.join("", "static", requestTarget))
        ).toURI());
        return Files.readAllBytes(resourcePath);
    }
}
