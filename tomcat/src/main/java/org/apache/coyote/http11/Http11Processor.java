package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            if (!validateHeaders(bufferedReader)) {
                return;
            }

            final String[] requestComponents = requestLine.split(" ");
            final String requestUri = requestComponents[1];
            final String responseBody = getResponseBody(requestUri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestUri) throws IOException {
        final String responseBody;
        if ("/".equals(requestUri)) {
            responseBody = "Hello world!";
        } else {
            final String resourceName = "static" + requestUri;
            final String fileName = Objects.requireNonNull(
                    getClass().getClassLoader().getResource(resourceName)
            ).getPath();

            responseBody = Files.readString(Path.of(fileName));
        }
        return responseBody;
    }

    private static boolean validateHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                return false;
            }
            line = bufferedReader.readLine();
        }
        return true;
    }
}
