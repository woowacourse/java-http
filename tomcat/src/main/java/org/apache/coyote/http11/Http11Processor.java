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

            final var requestLine = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).readLine();
            var requestUri = "/";
            var responseBody = "Hello world!";
            var contentType = "text/html;charset=utf-8";

            if (requestLine != null) {
                requestUri = requestLine.split(" ")[1];
            }

            if ("/index.html".equals(requestUri) || "/css/styles.css".equals(requestUri)) {
                try (final var resource = getClass().getClassLoader().getResourceAsStream("static" + requestUri)) {
                    responseBody = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
                }
            }

            if (requestUri.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
