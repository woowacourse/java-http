package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();

            byte[] bytes;
            final String requestUri = line.split(" ")[1];

            if (requestUri.equals("/")) {
                bytes = "Hello world!".getBytes(StandardCharsets.UTF_8);
            } else {
                final URL resource = getClass().getClassLoader()
                        .getResource("static" + requestUri);
                final Path path = Path.of(resource.getFile());
                bytes = Files.readAllBytes(path);
            }

            String responseHeader;
            if (requestUri.contains("css")) {
                responseHeader = getHeader(bytes, "css");
            } else {
                responseHeader = getHeader(bytes, "html");
            }

            outputStream.write(responseHeader.getBytes());
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getHeader(final byte[] bytes, String type) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                ""
        ) + "\r\n";
    }
}
