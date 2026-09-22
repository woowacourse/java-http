package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

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
            final var requestPath = readRequestPath(inputStream);
            final var responseBody = readStaticResource(requestPath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType(requestPath) + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestPath(final InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final var requestLine = reader.readLine();
        if (requestLine == null) {
            return "/";
        }
        readHeaders(reader);
        return requestLine.split(" ")[1];
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            // 1단계에서는 요청 헤더를 소비만 한다.
        }
    }

    private String readStaticResource(final String requestPath) throws IOException {
        if (!requestPath.endsWith(".html") && !requestPath.endsWith(".css")) {
            return DEFAULT_RESPONSE_BODY;
        }

        try (final var resource = getClass().getResourceAsStream("/static" + requestPath)) {
            if (resource == null) {
                return DEFAULT_RESPONSE_BODY;
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String contentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
