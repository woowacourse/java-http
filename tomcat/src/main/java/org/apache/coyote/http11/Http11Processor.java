package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
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
        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            String[] requestLine = reader.readLine().split(" ");
            String path = requestLine[1];

            final var responseBody = getResponseBody(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String path) {
        if ("/".equals(path)) {
            return "Hello world!";
        }

        String resource = getResource(path);
        if (resource == null) {
            throw new RuntimeException("자원을 찾을 수 없습니다.");
        }

        return resource;
    }

    private String getResource(String path) {
        try (final var inputStream = Http11Processor.class
                .getClassLoader()
                .getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                return null;
            }
            
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
