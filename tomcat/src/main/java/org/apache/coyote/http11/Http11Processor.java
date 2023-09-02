package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
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
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream();
                final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String requestLine = reader.readLine();
            List<String> split = List.of(requestLine.split(" "));
            String requestTarget = split.get(1);

            Path path = getPath(requestTarget);
            File file = path.toFile();
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            String responseBody = getResponseBody(path);

            String statusLine = "HTTP/1.1 200 OK ";
            String contentType = String.format("Content-Type: text/%s;charset=utf-8 ", fileExtension);
            String contentLength = "Content-Length: " + responseBody.getBytes().length + " ";

            final var response = new StringBuilder()
                    .append(statusLine).append("\r\n")
                    .append(contentType).append("\r\n")
                    .append(contentLength).append("\r\n\r\n")
                    .append(responseBody)
                    .toString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    private Path getPath(String requestTarget) throws URISyntaxException {
        String uri = requestTarget.substring(1);
        URL resource = getClass().getClassLoader()
                .getResource(String.format("static/%s", uri));

        return Paths.get(Objects.requireNonNull(resource).toURI());
    }

}

