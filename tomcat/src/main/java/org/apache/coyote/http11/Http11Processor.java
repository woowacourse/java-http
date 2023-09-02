package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_DIRECTORY = "/";
    private static final String STATIC = "static";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", this.connection.getInetAddress(), this.connection.getPort());
        this.process(this.connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String firstLine = this.findFirstLine(reader);
            final String path = this.findPath(firstLine);

            final String responseBody = this.makeResponseBody(path);

            this.sendResponse(outputStream, responseBody);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String findFirstLine(final BufferedReader reader) throws IOException {
        final String firstLine = reader.readLine();
        if (firstLine.isBlank()) {
            throw new HttpException("Request is blank");
        }
        return firstLine;
    }

    private String findPath(final String firstLine) {
        final String[] tokens = firstLine.split(" ");
        final String path = tokens[1];
        return path;
    }

    private String makeResponseBody(final String path) throws IOException {
        if (path.equals(ROOT_DIRECTORY)) {
            return "Hello world!";
        }
        final URL resource = this.getClass().getClassLoader().getResource(STATIC + path);
        final Path actualFilePath = new File(resource.getPath()).toPath();
        final byte[] fileBytes = Files.readAllBytes(actualFilePath);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    private void sendResponse(final OutputStream outputStream, final String responseBody) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
