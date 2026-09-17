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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
            String line = reader.readLine();

            if (line == null) {
                return;
            }

            String[] requestLine = line.split(" ");

            String method = requestLine[0];
            String path = requestLine[1];
            String version = requestLine[2];

            String header;

            while ((header = reader.readLine()) != null && !header.isEmpty()) {
                System.out.println(header);
            }

            // 추후 requestLine(method/path) 검증 추가 예정

            URL resource = getClass()
                    .getClassLoader()
                    .getResource("static" + path);

            if (resource == null) {
                return;
            }

            byte[] body;
            try (InputStream resourceStream = resource.openStream()) {
                body = resourceStream.readAllBytes();
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.length + " ",
                    "",
                    ""
            );

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(body);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
