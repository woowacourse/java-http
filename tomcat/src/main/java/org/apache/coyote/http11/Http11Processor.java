package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final String requestLine = reader.readLine();
            final String requestPath = requestLine.split(" ")[1];

            while (!reader.readLine().isEmpty()) {

            }

            if ("/index.html".equals(requestPath)) {
                final byte[] responseBody = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static/index.html")
                        .readAllBytes();

                outputStream.write(response(responseBody, "text/html;charset=utf-8").getBytes(StandardCharsets.UTF_8));
            } else if ("/css/styles.css".equals(requestPath)) {
              final byte[] responseBody = getClass()
                      .getClassLoader()
                      .getResourceAsStream("static/css/styles.css")
                      .readAllBytes();

              outputStream.write(response(responseBody, "text/css;charset=utf-8").getBytes(StandardCharsets.UTF_8));
            } else {
                final var responseBody = "Hello world!";
                final byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(response(responseBodyBytes, "text/html;charset=utf-8").getBytes(StandardCharsets.UTF_8));
            }

            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String response(final byte[] responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody, StandardCharsets.UTF_8));
    }
}
