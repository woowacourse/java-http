package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String requestLine = bufferedReader.readLine();

            if (requestLine == null) {
                return;
            }

            String path = requestLine.split(" ")[1];

            String line = bufferedReader.readLine();

            while (!"".equals(line)) {
                if (line == null) {
                    return;
                }

                line = bufferedReader.readLine();
            }


            var responseBody = "Hello world!";
            var contentType = "text/html;charset=utf-8";

            if (path.equals("/index.html")) {
                URL resource = getClass().getClassLoader()
                        .getResource("static/index.html");

                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            }

            if (path.equals("/css/styles.css")) {
                URL resource = getClass().getClassLoader()
                        .getResource("static/css/styles.css");

                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                contentType = "text/css;charset=utf-8";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
