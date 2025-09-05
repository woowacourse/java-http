package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final String requestLine = reader.readLine();
            String[] parsedLine = requestLine.split(" ");
            String requestUrl = parsedLine[1];

            URL resource;
            if (requestUrl.endsWith(".html") || requestUrl.endsWith(".css")) {
                resource = getClass().getClassLoader().getResource("static" + requestUrl);
            } else {
                resource = getClass().getClassLoader().getResource(requestUrl);
            }

            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            String contentType = "text/plain;charset=utf-8";
            if (requestUrl.endsWith(".html")) {
                contentType = "text/html;charset=utf-8";
            } else if (requestUrl.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length,
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
