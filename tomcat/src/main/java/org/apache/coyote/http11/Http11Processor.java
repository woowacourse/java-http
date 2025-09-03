package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String startLine = bufferedReader.readLine();
            final String[] split = startLine.split(" ");
            final String requestTarget = split[1];
            String responseBody;
            String response;
            if (requestTarget.equals("/")) {
                responseBody = "Hello World!";

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            } else if (requestTarget.startsWith("/css")) {
                final URL resource = getClass().getClassLoader().getResource("static" + requestTarget);
                final Path path = Paths.get(resource.getPath());
                final List<String> strings = Files.readAllLines(path);
                responseBody = String.join("\r\n", strings);

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            } else {
                final URL resource = getClass().getClassLoader().getResource("static" + requestTarget);
                final Path path = Paths.get(resource.getPath());
                final List<String> strings = Files.readAllLines(path);
                responseBody = String.join("\r\n", strings);

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
