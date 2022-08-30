package org.apache.coyote.http11;

import java.io.BufferedReader;
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
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String fileName = bufferedReader.readLine().split(" ")[1];

            final var response = makeResponse(fileName);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(String fileName) throws IOException {
        String responseBody = getResponseBody(fileName);
        String contentType = "text/html";
        if (isCss(fileName)) {
            contentType = "text/css";
        }
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private boolean isCss(String fileName) {
        return fileName.contains("/css/");
    }

    private String getResponseBody(String fileName) throws IOException {
        if (fileName.equals("/") || fileName.isEmpty()) {
            return "Hello world!";
        }
        return getContent(fileName);
    }

    private String getContent(String fileName) throws IOException {
        Path path = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + fileName))
                .getFile());
        return Files.readString(path);
    }
}
