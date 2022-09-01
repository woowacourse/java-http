package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.StreamUtils;

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

            final String uri = StreamUtils.readAllLines(inputStream).split(" ")[1];

            final String responseBody = getResponseBody(uri);

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

    private String getResponseBody(final String fileName) {
        final List<String> actual;
        try {
            if ("/" .equals(fileName)) {
                return "Hello world!";
            }
//            final var responseBody = "Hello world!";
//            final Path path = Paths.get("./src/main/resources/static/index.html")
            final Path path = Paths.get("tomcat", "src", "main", "resources", "static", fileName)
                    .toAbsolutePath();
            final File file = path.toFile();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            actual = bufferedReader.lines().collect(Collectors.toCollection(LinkedList::new));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return actual.stream().collect(Collectors.joining("\r\n"));
    }
}
