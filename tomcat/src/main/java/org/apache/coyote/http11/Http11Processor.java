package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            List<String> request = new ArrayList<>();
            String line = null;
            while (!"".equals(line)) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                request.add(line);
            }

            var contentType = "html";
            for (String text : request) {
                if (text.startsWith("Accept:")) {
                    if (text.contains("css")) {
                        contentType = "css";
                    }
                    if (text.contains("js")) {
                        contentType = "javascript";
                    }
                }
            }

            final var requestURI = request.get(0).split(" ")[1];
            var responseBody = "Hello world!";
            if (!"/".equals(requestURI)) {
                final Path path =
                        new File(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestURI))
                                .getFile()
                        ).toPath();

                responseBody = new String(Files.readAllBytes(path));
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + contentType + ";charset=utf-8 ",
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
