package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
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
        try (final var inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {

            String requestLine = inputStream.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            if(method.equals("GET")) {
                if(path.equals("/")) {
                    final var responseBody = "Hello world!";
                    final var response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }
                if (path.equals("/index.html")) {
                    try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
                        if (fileInputStream == null) {
                            return;
                        }
                        byte[] bodyBytes = fileInputStream.readAllBytes();
                        final var response = String.join("\r\n",
                                "HTTP/1.1 200 OK ",
                                "Content-Type: text/html;charset=utf-8 ",
                                "Content-Length: " + bodyBytes.length + " ",
                                "","");

                        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                        outputStream.write(bodyBytes);
                        outputStream.flush();
                    }
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
