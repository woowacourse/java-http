package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var streamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(streamReader);
             final var outputStream = connection.getOutputStream()) {

            String requestLine = bufferedReader.readLine();
            String[] splitRequestLine = requestLine.split(" ");

            String method = splitRequestLine[0];
            String requestTarget = splitRequestLine[1];
            String protocol = splitRequestLine[2];

            if (method.equals("GET")) {

                final byte[] responseBody;
                if (requestTarget.equals("/")) {
                    responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                } else {
                    final String resourceName = requestTarget.substring(1);
                    final URL resource = getClass().getClassLoader().getResource("static/" + resourceName);
                    final Path path = new File(resource.getFile()).toPath();
                    responseBody = Files.readAllBytes(path);
                }

                final var header = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.length + " ",
                        "",
                        "");

                outputStream.write(header.getBytes());
                outputStream.write(responseBody);
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
