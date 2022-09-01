package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // 1. index.html 응답하기
            String line = bufferedReader.readLine();
            final String[] requestLine = line.split(" ");
            final String requestUri = requestLine[1];
            var responseBody = "Hello world!";

            if (!requestUri.equals("/")) {
                final Path path = new File(
                        getClass().getClassLoader().getResource("static" + requestUri).getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
            }

            // 2. CSS 지원하기
            final Map<String, String> requestHeader = new HashMap<>();
            line = bufferedReader.readLine();
            while (!"".equals(line)) {
                final String[] headerField = line.split(": ");
                requestHeader.put(headerField[0], headerField[1]);
                line = bufferedReader.readLine();
            }
            final String contentType = requestHeader.getOrDefault("Accept", "text/html").split(",")[0];

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
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
