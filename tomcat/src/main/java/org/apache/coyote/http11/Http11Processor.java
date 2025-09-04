package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
                final var inputReader = new BufferedReader(new InputStreamReader(inputStream));
                final var outputStream = connection.getOutputStream()
        ) {
            final String line = inputReader.readLine();
            if (line == null || line.isBlank()) {
                final String responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            final String requestUri = line.split(" ")[1];
            if (requestUri.equals("/")) {
                final String responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            final String resourcePath = "static" + requestUri;
            try (
                    final var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)
            ) {
                if (resourceStream == null) {
                    return;
                }
                final byte[] responseBody = resourceStream.readAllBytes();
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + "text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.length + " ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.write(responseBody);
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
