package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // URL 파싱
            String line = bufferedReader.readLine();
            final String[] tokens = line.split(" ", 3);
            String method = tokens[0];
            final String requestTarget = tokens[1];
            String httpVersion = tokens[2];

            final var responseBody = createResponseBody(requestTarget);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] createResponseBody(String requestTarget) throws IOException, URISyntaxException {
        final String resourcePath = "static" + requestTarget;

        if (requestTarget.equals("/")) {
            return "Hello world!".getBytes();
        }

        final URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readAllBytes(path);
    }
}
