package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            final var requestPath = parseRequestPath(inputStream);

            final byte[] responseBody;
            if (requestPath.equals("/")) {
                responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            } else {
                responseBody = readStaticResource(requestPath);
            }

            writeResponse(outputStream, responseBody);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestPath(final InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));

        final var line = reader.readLine();

        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("HTTP 요청 라인이 비어있습니다.");
        }

        String[] token = line.trim().split("\\s+");

        if (token.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 라인입니다.");
        }

        return token[1];
    }

    private byte[] readStaticResource(final String path) throws IOException {
        final var resourcePath = "static" + path;

        final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다." + resourcePath);
        }

        try (resource) {
            return resource.readAllBytes();
        }
    }

    private void writeResponse(final OutputStream outputStream, final byte[] bytes) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "\r\n");
        outputStream.write(response.getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }
}
