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

            final var request = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            System.out.println(request);

            final var uri = request.split(" ")[1];
            System.out.println(uri);

            final var responseBody = getResponseBody(uri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    getContentType(uri),
                    getContentLength(responseBody),
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(final String uri) throws IOException {
        if ("/".equals(uri)) {
            return "Hello world!";
        }

        final URL resource = ClassLoader.getSystemClassLoader().getResource("static" + uri);
        final String file = Objects.requireNonNull(resource).getFile();
        return new String(Files.readAllBytes(new File(file).toPath()));
    }

    private String getContentType(final String uri) {
        if (uri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }

        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String getContentLength(final String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + " ";
    }
}
