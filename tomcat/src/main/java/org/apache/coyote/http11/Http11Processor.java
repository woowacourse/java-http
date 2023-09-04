package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
             final var outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String firstLine = reader.readLine();
            if (firstLine == null) {
                return;
            }
            final StringBuilder request = new StringBuilder(firstLine);

            String line;
            while (!Objects.equals(line = reader.readLine(), "")) {
                request.append(line).append("\r\n");
            }

            String fileName = extractResourcePathFromRequest(request);


            var responseBody = "";

            if (fileName.length() == 0) {
                responseBody = "Hello world!";
            } else {
                final Path path = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/" + fileName)).getPath());

                responseBody = new String(Files.readAllBytes(path));
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractResourcePathFromRequest(StringBuilder request) {
        int startIndex = request.indexOf("GET ") + 4;
        int endIndex = request.indexOf(" HTTP/1.1");

        if (startIndex != -1 && endIndex != -1) {
            return request.substring(startIndex + 1, endIndex);
        }

        return "";
    }
}
