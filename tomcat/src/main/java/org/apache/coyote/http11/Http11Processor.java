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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final var requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final var requestParts = requestLine.split(" ");
            if (requestParts.length < 3) {
                return;
            }
            final var path = requestParts[1];

            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                // Read through the blank line that ends the request headers.
            }
            if (headerLine == null) {
                return;
            }

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            final boolean isCss = "/css/styles.css".equals(path);
            final String contentType = isCss ? "text/css;charset=utf-8" : "text/html;charset=utf-8";
            if ("/index.html".equals(path) || isCss) {
                final String resourcePath = "static" + path;
                try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                    if (resource == null) {
                        throw new IOException(resourcePath + " not found");
                    }
                    responseBody = resource.readAllBytes();
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
