package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final RequestLine requestLine = readRequestLine(inputStream);
            final String requestTarget = requestLine.requestTarget();
            final String resourcePath = "static" + requestLine.requestTarget();
            var responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);

            try (InputStream resourceStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(resourcePath)) {

                if (resourceStream != null && !requestTarget.equals("/")) {
                    responseBody = resourceStream.readAllBytes();
                }
            }

            final String responseHead = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(responseHead.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestLine readRequestLine(InputStream inputStream) throws IOException {
        final BufferedReader reader = getReader(inputStream);
        String line = reader.readLine();

        if (line == null) {
            throw new IllegalStateException("request line is null");
        }

        return RequestLine.from(line);
    }

    private BufferedReader getReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }
}
