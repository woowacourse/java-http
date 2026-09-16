package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css";

    private record ResponseContent(String body, String contentType) {
    }

    private ResponseContent responseContentFor(final String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return new ResponseContent("Hello world!", HTML_CONTENT_TYPE);
        }

        final var resourcePath = "static" + requestPath;

        try (final var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)){
            if (resourceStream == null) {
                return new ResponseContent("Hello world!", HTML_CONTENT_TYPE);
            }

            final var body = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
            return new ResponseContent(body, contentTypeFor(requestPath));
        }
    }

    private String contentTypeFor(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }

        return HTML_CONTENT_TYPE;
    }

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

            final var reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
            final var requestHeader = RequestHeader.from(reader);
            final var responseContent = responseContentFor(requestHeader.path());

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + responseContent.contentType() + " ",
                    "Content-Length: " +  responseContent.body().getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseContent.body());

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
