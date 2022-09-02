package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            String response = generateResponse(firstLine);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(String firstLine) throws IOException {
        String responseBody = generateResponseBody(firstLine);
        String contentType = getContentType(firstLine);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getContentType(String firstLine) {
        if (firstLine.contains("/css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        if (firstLine.contains("/js")) {
            return "Content-Type: text/js;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String generateResponseBody(String firstLine) throws IOException {
        if ("/".equals(firstLine.split(" ")[1])) {
            return DEFAULT_RESPONSE_BODY;
        }

        String fileName = firstLine.split(" ")[1];
        Path path = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("static" + fileName)).getFile()
        ).toPath();

        return Files.readString(path);
    }
}
