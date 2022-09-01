package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String uriPath = inputBufferedReader.readLine().split(" ")[1];

            var responseBody = "Hello world!";
            String contentType = "text/html";
            if (!uriPath.equals("/")) {
                String extension = uriPath.split("\\.")[1];
                if (extension.equals("css")) {
                    contentType = "text/css";
                }
                if (extension.equals("js")) {
                    contentType = "application/x-javascript";
                }
                if (extension.equals("ico")) {
                    contentType = "image/x-icon";
                }
                String fileName = "static" + uriPath;
                final URL resource = getClass().getClassLoader().getResource(fileName);
                final File file = Paths.get(resource.toURI()).toFile();
                responseBody = new String(Files.readAllBytes(file.toPath()));
            }
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
