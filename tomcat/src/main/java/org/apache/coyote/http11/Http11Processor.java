package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {
            final List<String> request = extractRequest(inputStream);
            String responseBody = handler(extractUri(request));
            outputStream.write(writeResponseOk(responseBody));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> extractRequest(InputStream inputStream) throws IOException {
        List<String> request = new ArrayList<>();
        final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            request.add(line);
        }
        return request;
    }

    private String extractUri(List<String> request) {
        return request.get(0).split(" ")[1];
    }

    private String handler(String uri) throws IOException {
        if ("/".equals(uri)) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(
            new File(Objects.requireNonNull(getResource(uri)).getFile()).toPath()));
    }

    private URL getResource(String uri) {
        return getClass().getClassLoader()
            .getResource("static" + uri);
    }

    private byte[] writeResponseOk(String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody).getBytes();
    }
}
