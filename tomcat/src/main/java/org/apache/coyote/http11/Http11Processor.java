package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var line = bufferedReader.readLine();

            if (line == null) {
                throw new IllegalStateException("잘못된 요청입니다.");
            }

            final var fileName = line.split(" ")[1];

            final var fileType = fileName.split("\\.")[1];

            String contentType = "";

            if (fileType.equals("html")) {
                contentType = "text/html";
            }

            if (fileType.equals("css")) {
                contentType = "text/css";
            }

            if (fileType.equals("js")) {
                contentType = "text/javascript";
            }

            final var resource = getClass().getClassLoader().getResource("static" + fileName);


            final var path = new File(resource.getPath()).toPath();

            final var responseBody = new String(Files.readAllBytes(path));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType +";charset=utf-8 ",
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
