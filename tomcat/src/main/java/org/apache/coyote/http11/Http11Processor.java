package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            List<String> requestLines = extractRequestLines(inputStream);
            String requestURI = extractResourceName(requestLines);
            URL resource = getClass().getClassLoader().getResource("static/" + requestURI);

            assert resource != null;
            final Path path = Paths.get(resource.getPath());
            List<String> contents = Files.readAllLines(path);
            final var responseBody = String.join("", contents);

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

    private List<String> extractRequestLines(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> requestLines = new ArrayList<>();

        String line = bufferedReader.readLine();
        if (line == null) {
            return requestLines;
        }
        while (!"".equals(line)) {
            requestLines.add(line);
            log.info("request line {}", line);
            line = bufferedReader.readLine();
        }
        return requestLines;
    }

    private String extractResourceName(List<String> requestLines) {
        String requestHead = requestLines.get(0);
        return requestHead.split(" ")[1];
    }
}
