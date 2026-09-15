package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            final String path = requestLine.split(" ")[1];

            final String response = createResponse(path);

            outputStream.write(response.getBytes(UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String path) throws IOException, URISyntaxException {
        if (path.equals("/")) {
            return response("HTTP/1.1 200 OK ", "text/html", "Hello world!");
        }

        final var resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            final var notFound = getClass().getClassLoader().getResource("static/404.html");
            return response("HTTP/1.1 404 Not Found ", "text/html", readResource(notFound));
        }

        return response("HTTP/1.1 200 OK ", contentType(path), readResource(resource));
    }

    private String readResource(final URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()), UTF_8);
    }

    private String response(final String statusLine, final String contentType, final String body) {
        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes(UTF_8).length + " ",
                "",
                body);
    }

    private String contentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}
