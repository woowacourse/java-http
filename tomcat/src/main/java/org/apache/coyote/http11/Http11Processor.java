package org.apache.coyote.http11;

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            String uri = requestLine.split(" ")[1];
            if ("/".equals(uri)) {
                final var responseBody = "Hello world!";

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
            String resourcePath = uri.substring(1);
            try (InputStream fileInputStream = getClass().getClassLoader()
                    .getResourceAsStream("static/" + resourcePath)) {

                if (fileInputStream == null) {

                    String responseBody = "404 Not Found";
                    byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

                    final var response = String.join("\r\n",
                            "HTTP/1.1 404 Not Found",
                            "Content-Type: text/plain;charset=utf-8",
                            "Content-Length: " + bodyBytes.length,
                            "");

                    outputStream.write(response.getBytes());
                    outputStream.write(bodyBytes);
                } else {
                    String contentType = "text/html;charset=utf-8"; // 기본값은 html
                    if (uri.endsWith(".css")) {
                        contentType = "text/css";
                    }
                    if (uri.endsWith(".js")) {
                        contentType = "text/javascript";
                    }

                    byte[] bodyBytes = fileInputStream.readAllBytes();

                    final var response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: " + contentType + " ",
                            "Content-Length: " + bodyBytes.length + " ",
                            "",
                            "");
                    outputStream.write(response.getBytes());
                    outputStream.write(bodyBytes);
                }
                outputStream.flush();
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
