package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            final var fileName = getFileName(inputStream);
            final var responseBody = getResponseBody(fileName);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(fileName) + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFileName(final InputStream inputStream) {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            final var line = bufferedReader.readLine();
            final var chunks = line.split(" ");

            return chunks[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResponseBody(final String fileName) {
        if (fileName.equals("/")) {
            return "Hello world!";
        }
        final var resource = ClassLoader.getSystemResource("static" + fileName);

        if (resource == null) {
            return "Not found: " + fileName;
        }
        try {
            final var path = Paths.get(resource.getPath());

            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getContentType(final String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (fileName.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (fileName.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        // 이외의 MIME 타입 저장 가능
        return "text/html;charset=utf-8";
    }
}
