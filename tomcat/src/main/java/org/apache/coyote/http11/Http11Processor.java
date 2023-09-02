package org.apache.coyote.http11;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DELIMITER = " ";
    private static final int URL_INDEX = 1;
    private static final String DEFAULT_BODY_MESSAGE = "Hello world!";
    private static final String EMPTY_URL = "/";
    private static final String PATH = "static";

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
                final var outputStream = connection.getOutputStream()
        ) {
            String content = readAsUTF8(inputStream);
            String url = parseURL(content);
            String resource = mapToResource(url);

            String body = makeResponseBody(resource);
            final String response = makeResponse(body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readAsUTF8(InputStream inputStream) {
        String content = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            content = reader.lines()
                    .collect(joining());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return content;
    }

    private String parseURL(String content) {
        List<String> lines = Arrays.stream(content.split(DELIMITER))
                .collect(toList());
        return lines.get(URL_INDEX);
    }

    private String mapToResource(String url) throws IOException {
        if (Objects.equals(url, EMPTY_URL)) {
            return null;
        }

        final URL locate = getClass().getClassLoader().getResource(PATH + url);
        if (locate != null) {
            return new String(Files.readAllBytes(new File(locate.getFile()).toPath()));
        }
        return null;
    }

    private String makeResponseBody(String resource) throws IOException {
        return Objects.requireNonNullElse(resource, DEFAULT_BODY_MESSAGE);
    }

    private String makeResponse(String responseBody) {
        return String.join(
                System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
