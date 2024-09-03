package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

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

            // 1. Request
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);

            // 1.1. Request Line
            final var requestLine = bufferedReader.readLine();
            final var requestLineSplit = requestLine.split(" ");
            final var _method = requestLineSplit[0];
            final var path = requestLineSplit[1];
            final var _protocolVersion = requestLineSplit[2];

            final var filename = path.replace("/", "");

            // 1.1.1. Find Static Resource
            String responseBody;
            if (!filename.isEmpty()) {
                final var classLoader = getClass().getClassLoader();
                final var url = classLoader.getResource("static/" + filename);
                final var resourcePath = Path.of(url.getPath());

                responseBody = Files.readString(resourcePath);

            } else {
                responseBody = "Hello world!";
            }

            // 2. Response
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
