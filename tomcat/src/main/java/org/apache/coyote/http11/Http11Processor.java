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
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final String requestLine = reader.readLine();
            log.info("request line: {}", requestLine);

            if (requestLine == null) {
                return;
            }

            final String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            final String method = parts[0];
            final String requestTarget = parts[1];
            final String httpVersion = parts[2];

            log.info("method: {}, target: {}, version: {}",
                    method, requestTarget, httpVersion);

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String contentType = "text/html;charset=utf-8";

            if ("/index.html".equals(requestTarget)
                    || "/css/styles.css".equals(requestTarget)) {

                if ("/css/styles.css".equals(requestTarget)) {
                    contentType = "text/css;charset=utf-8";
                }

                final String resourcePath = "static" + requestTarget;
                try (var resource = getClass().getClassLoader()
                        .getResourceAsStream(resourcePath)) {

                    if (resource == null) {
                        throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
                    }

                    responseBody = resource.readAllBytes();
                }
            }

            final String responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
