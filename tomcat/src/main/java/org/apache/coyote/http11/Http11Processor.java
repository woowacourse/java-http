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
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int MIN_REQUEST_LINE_PARTS = 3;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final String INDEX_PATH = "/index.html";
    private static final String CSS_PATH = "/css/styles.css";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String CRLF = "\r\n";

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final var requestTarget = readRequestTarget(reader);
            if (requestTarget == null) {
                return;
            }

            writeResponse(outputStream, createResponse(requestTarget));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestTarget(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }

        final var requestParts = requestLine.split(" ");
        if (requestParts.length < MIN_REQUEST_LINE_PARTS) {
            return null;
        }
        if (!consumeHeaders(reader)) {
            return null;
        }
        return requestParts[REQUEST_TARGET_INDEX];
    }

    private boolean consumeHeaders(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private HttpResponse createResponse(final String requestTarget) throws IOException {
        if (INDEX_PATH.equals(requestTarget)) {
            return new HttpResponse(HTML_CONTENT_TYPE, readResource(requestTarget));
        }
        if (CSS_PATH.equals(requestTarget)) {
            return new HttpResponse(CSS_CONTENT_TYPE, readResource(requestTarget));
        }
        return new HttpResponse(HTML_CONTENT_TYPE, "Hello world!".getBytes(StandardCharsets.UTF_8));
    }

    private byte[] readResource(final String requestTarget) throws IOException {
        final var resourcePath = "static" + requestTarget;
        try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException(resourcePath + " not found");
            }
            return resource.readAllBytes();
        }
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        final var headers = String.join(CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + response.contentType() + " ",
                "Content-Length: " + response.body().length + " ",
                "",
                "");

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(response.body());
        outputStream.flush();
    }

    private record HttpResponse(String contentType, byte[] body) {
    }
}
