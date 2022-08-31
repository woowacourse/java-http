package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

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

            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(input);

            String uri = request.getUri();

            var responseBody = "Hello world!";
            if (!uri.equals("/")) {
                responseBody = readFile(uri);
            }

            HttpResponse response = new HttpResponse(
                request.getVersion(),
                "200 OK",
                uri,
                responseBody
            );

            outputStream.write(response.toResponseString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readFile(String uri) {
        try {
            final Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + uri)
                .toURI());

            final List<String> contents = Files.readAllLines(path);

            StringBuilder result = new StringBuilder();
            for (String content : contents) {
                result.append(content + "\n");
            }

            return result.toString();
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
