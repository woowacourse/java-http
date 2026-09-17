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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            readHeaders(reader);

            String requestUri = extractRequestUri(requestLine);
            InputStream resourceStream = getResourceStream(requestUri);

            String responseBody = getResponseBody(requestUri, resourceStream);
            String response = createResponse(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            line = reader.readLine();
        }
    }

    private String extractRequestUri(final String requestLine) {
        String[] request = requestLine.split(" ");
        return request[1];
    }

    private InputStream getResourceStream(final String requestUri) {
        String resourcePath = "static" + requestUri;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(resourcePath);
    }

    private static String getResponseBody(String requestUri, InputStream resourceStream) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }
        return new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String createResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
