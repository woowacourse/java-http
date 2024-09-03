package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

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
            String uri = extractURIByRequest(inputStream);
            final URL resource = getClass().getClassLoader().getResource(uri);
            if (resource == null) {
                writeAndFlush(uri.substring("static".length()), outputStream);
                return;
            }
            final String content = new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
            writeAndFlush(content, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void writeAndFlush(String content, OutputStream outputStream) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public static String extractURIByRequest(InputStream inputStream) throws IOException {
        consumeHeader(inputStream);
        String uri = getUri(inputStream);
        if (uri.equals("/")) {
            return "static/index.html";
        }
        return "static" + uri;
    }

    private static void consumeHeader(InputStream inputStream) throws IOException {
        while ((inputStream.read()) != ' ') {
        }
    }

    private static String getUri(InputStream inputStream) throws IOException {
        int ch;
        StringBuilder uriBuilder = new StringBuilder();
        while ((ch = inputStream.read()) != ' ') {
            uriBuilder.append((char) ch);
        }
        return uriBuilder.toString();
    }
}
