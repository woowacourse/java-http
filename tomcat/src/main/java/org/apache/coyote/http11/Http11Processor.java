package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
             final var outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String firstLine = br.readLine();

            String uri = firstLine.split(" ")[1];
            final URL resource = getClass().getClassLoader().getResource("static" + uri);

            if ("/".equals(uri)) {
                final var responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            } else if (resource == null) {
                final URL notFound = getClass().getClassLoader().getResource("static/404.html");
                Path filePath = new File(notFound.getFile()).toPath();

                final String contentType = Files.probeContentType(filePath);
                final var responseBody = new String(Files.readAllBytes(filePath));

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            } else {
                Path filePath = new File(resource.getFile()).toPath();

                final String contentType = Files.probeContentType(filePath);
                final var responseBody = new String(Files.readAllBytes(filePath));

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
