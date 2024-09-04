package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            var result = "";
            final String requestMethodAndUrl = bufferedReader.readLine();

            log.info("GET 요청 = {}", requestMethodAndUrl);
            final String[] texts = requestMethodAndUrl.split(" ");
            final var path = texts[1];

            final Request request = new Request(path);
            final URL resource = request.getUrl();
            result = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var response = String.join("\r\n", "HTTP/1.1 200 OK ",
                    "Content-Type: " + request.getContentType() + ";charset=utf-8 ",
                    "Content-Length: " + result.getBytes().length + " ",
                    "",
                    result);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
