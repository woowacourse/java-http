package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream()) {

            String uri = bufferedReader.readLine().split(" ")[1];
            String resourcePath = uri;

            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                resourcePath = uri.substring(0, index);
            }

            if (resourcePath.equals("/")) {
                final var defaultBody = "Hello world!";

                final var defaultResponse = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + defaultBody.getBytes().length + " ",
                        "",
                        defaultBody);

                outputStream.write(defaultResponse.getBytes());
                outputStream.flush();

                return;
            }

            final Path path = Path.of(getClass().getClassLoader().getResource("static/" + resourcePath).getPath());
            final List<String> file = Files.readAllLines(path);
            String mimeType = Files.probeContentType(path);

            StringBuilder stringBuilder = new StringBuilder();
            for (String body : file) {
                stringBuilder.append(body).append("\r\n");
            }
            final var responseBody = stringBuilder.toString();

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + mimeType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
