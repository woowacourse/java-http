package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
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
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            String responseBody = "Hello world!";
            String uri = "/";
            String requestLine = bufferedReader.readLine();
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length >= 2) {
                    uri = requestParts[1].trim();
                }
            }

            if (uri.equals("/index.html")) {
                final String fileName = "static/index.html";
                final URL url = getClass().getClassLoader().getResource(fileName);
                if (url != null) {
                    final File file = new File(url.getFile());
                    final Path path = file.toPath();

                    final StringBuilder htmlContent = new StringBuilder();
                    try (BufferedReader htmlBufferedReader = new BufferedReader(new FileReader(path.toString()))) {
                        String line;
                        while ((line = htmlBufferedReader.readLine()) != null) {
                            htmlContent.append(line).append("\n");
                        }
                    }
                    responseBody = htmlContent.toString();
                }
            }

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
}
