package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String request = bf.readLine();
            String path = request.split(" ")[1];
            String responseBody = "Hello world!";

            if(path.equals("/index.html")) {
                try(final InputStream resourceStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static" + path)) {

                    if(resourceStream == null) {
                        throw new IllegalArgumentException("리소스를 찾을 수 없습니다: " + "static" + path);
                    }
                    responseBody = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
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
