package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            while (true) {
                String headerLine = reader.readLine();

                if (headerLine == null) {
                    return;
                }

                if (headerLine.isEmpty()) {
                    break;
                }
            }

            String[] requestParts = requestLine.split(" ");
            String requestUri = requestParts[1];

            byte[] responseBody = readResponseBody(requestUri);
            String contentType = resolveContentType(requestUri);

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

    private String resolveContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private byte[] readResponseBody(String requestUri) throws IOException {
        if ("/".equals(requestUri)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        String resourceName = "static" + requestUri;
        try (InputStream resourceStream =
                     Http11Processor.class
                             .getClassLoader()
                             .getResourceAsStream(resourceName)) {

            return Objects.requireNonNull(resourceStream).readAllBytes();
        }
    }
}
