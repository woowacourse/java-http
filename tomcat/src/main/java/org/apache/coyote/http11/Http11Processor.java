package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
            if ("/index.html".equals(path)) {
                final var resourcePath = Objects.requireNonNull(
                                getClass().getClassLoader()
                                        .getResource("static/index.html"))
                        .getPath();

                responseBody = Files.readAllBytes(Path.of(resourcePath));
            } else {
                responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            }

            final var responseHeaders = String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    ""
            );

            outputStream.write((responseHeaders + "\r\n").getBytes());
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
