package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";

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
        try (final var reader = new BufferedReader(
                 new InputStreamReader(
                         connection.getInputStream(),
                         StandardCharsets.UTF_8
                 ));
             final var outputStream = connection.getOutputStream()) {

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            readHeaders(reader);
            log.info("request: {}", requestLine);

            final String url = requestLine.split(" ")[1];

            if ("/".equals(url)) {
                writeResponse(outputStream, "200 OK", "Hello world!".getBytes(StandardCharsets.UTF_8));
                return;
            }

            final Optional<Path> staticFile = findStaticFile(url);
            if (staticFile.isEmpty()) {
                writeResponse(outputStream, "404 Not Found", readNotFoundBody());
                return;
            }
            writeResponse(outputStream, "200 OK", Files.readAllBytes(staticFile.get()));
        } catch (IOException | UncheckedServletException |URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            line = reader.readLine();
        }
    }

    private Optional<Path> findStaticFile(final String url) throws URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + url);
        if (resource == null) {
            return Optional.empty();
        }

        final Path path = Path.of(resource.toURI());
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    private byte[] readNotFoundBody() throws IOException, URISyntaxException {
        final Optional<Path> notFoundPage = findStaticFile("/404.html");
        if (notFoundPage.isPresent()) {
            return Files.readAllBytes(notFoundPage.get());
        }

        return "Not Found".getBytes(StandardCharsets.UTF_8);
    }

    private void writeResponse(final OutputStream outputStream, final String status, final byte[] body) throws IOException {
        final String header = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
