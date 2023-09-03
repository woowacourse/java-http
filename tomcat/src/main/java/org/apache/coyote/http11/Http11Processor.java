package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.StringJoiner;

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            String body = createBody(line);
            String header = createHeader(line, body);

            outputStream.write((header + body).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createBody(String request) throws IOException {
        String path = null;

        for (String element : request.split(" ")) {
            if (element.startsWith("/")) {
                path = element;
            }
        }

        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final URL resource = systemClassLoader.getResource("static" + path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String createHeader(String request, String body) {

        StringJoiner stringJoiner = new StringJoiner("\r\n");
        String contentType = "text/html;charset=utf-8";

        if (request.contains(".css")) {
            contentType = "text/css;charset=utf-8";
        }

        stringJoiner.add("HTTP/1.1 200 OK ");
        stringJoiner.add("Content-Type: " + contentType + " ");
        stringJoiner.add("Content-Length: " + body.getBytes().length + " ");
        stringJoiner.add("\r\n");
        return stringJoiner.toString();
    }
}
