package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
             final var outputStream = connection.getOutputStream()) {

            final InputStreamReader reader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(reader);
            final List<String> request = new ArrayList<>();
            String line;
            do {
                line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                request.add(line);
            } while (!"".equals(line));

            String responseBody = "Hello world!";
            final String requestFileName = request.get(0).split(" ")[1];
            if (requestFileName.length() > 1) {
                final URL url = getClass().getClassLoader().getResource("static/" + requestFileName);
                if (url != null) {
                    final var path = Paths.get(url.toURI());
                    responseBody = Files.readString(path);
                }
            }

            final String response;
            if (request.get(0).split(" ")[1].endsWith(".css")) {
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
            else {
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
