package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.exception.util.ErrorResourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Response.class);

    private static final String CRLF = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String LOCATION = "Location";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final OutputStream outputStream;

    private String version = HTTP_VERSION;
    private int code = 200;
    private String message = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public Http11Response(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send() throws IOException {
        final String statusLine = String.join(" ", version, String.valueOf(code), message) + CRLF;
        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));

        for (final Entry<String, String> entry : headers.entrySet()) {
            final String header = entry.getKey() + ": " + entry.getValue() + CRLF;
            outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        }

        outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));

        if (body != null) {
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        }

        outputStream.flush();
    }

    public void sendRedirect(final String path) throws IOException {
        setStatus(Http11Status.FOUND);
        putHeader(LOCATION, path);
        send();
    }

    public void sendError(final Http11Status status) throws IOException {
        try {
            setStatus(status);
            final String resourcePath = ErrorResourceMapper.getResource(status.getCode());
            readFileFromClasspath(resourcePath);
        } catch (final IOException e) {
            log.error("Failed to send error page for status {}. reason: {}", status, e.getMessage());
            setStatus(status);
            final String message = status.getStatusLine().split(" ", 3)[2];
            setBody(status.getCode() + " " + message);
            putHeader(CONTENT_TYPE, "text/plain; charset=utf-8");
            putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        }
        send();
    }

    public void readFileFromClasspath(final String resourcePath) throws IOException {
        final InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        final StringBuilder fileContents = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(CRLF);
            }
        }
        setBody(fileContents.toString());
        putHeader(CONTENT_TYPE, MediaType.detectMimeType(resourcePath));
        putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void setStatus(final Http11Status status) {
        this.code = status.getCode();
        this.message = status.getStatusLine().split(" ", 3)[2];
    }

    public void putHeader(final String name, final String value) {
        this.headers.put(name, value);
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
