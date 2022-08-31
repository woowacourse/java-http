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

            BufferedReader request = new BufferedReader(new InputStreamReader(inputStream));

            String inputUri = request.readLine().split(" ")[1];

            var responseBody = "Hello world!";

            if (!inputUri.equals("/")) {
                responseBody = readFile(inputUri);
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

    private String readFile(String inputUri) {
        try {
            final Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + inputUri)
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
