package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
            String urlPath = reader.readLine().split(" ")[1];
            if(urlPath.equals("/index.html")) {
                printFileResource("static/index.html", outputStream);
                return;
            }
            if(urlPath.startsWith("/css") || urlPath.startsWith("/js") || urlPath.startsWith("/assets")) {
                printFileResource("static" + urlPath, outputStream);
                return;
            }

            final var responseBody = "Hello world!";

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

    private void printFileResource(String fileName, OutputStream outputStream) {
        final URL url = getClass().getClassLoader().getResource(fileName);
        final File file = new File(url.getPath());
        final Path path = file.toPath();

        try {
            String contentType = "text/html";
            if(fileName.endsWith("css")) {
                contentType = "text/css";
            }
            final var responseBody = String.join("\n", Files.readAllLines(path));
            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.length() + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
