package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));

            final var requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final var parts = requestLine.split(" ");
            final var method = parts[0];
            final var path = parts[1];

            final byte[] responseBody;
            final String contentType;

            final var resourcePath = "static" + path;
            final var resourceUrl = getClass().getClassLoader()
                    .getResource(resourcePath);

            if (resourceUrl != null) {
                responseBody = Files.readAllBytes(Path.of(resourceUrl.toURI()));
                contentType = getContentType(path);
            } else {
                responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                contentType = "text/plain;charset=utf-8 ";
            }

            final var responseHeaders = String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.length + " ",
                    ""
            );

            outputStream.write((responseHeaders + "\r\n").getBytes());
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/plain;charset=utf-8 ";
    }
}
