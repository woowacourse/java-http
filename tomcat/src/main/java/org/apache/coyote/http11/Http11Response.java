package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Http11Response {

    private StatusLine statusLine;
    private final Map<String, List<String>> headers;
    private byte[] body;

    public Http11Response() {
        this.statusLine = StatusLine.from(200);
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public void setStatus(final int statusCode) {
        this.statusLine = StatusLine.from(statusCode);
    }

    public void setHeader(
            final String name,
            final String value
    ) {
        this.headers.put(name, new ArrayList<>(List.of(value)));
    }

    public void addHeader(
            final String name,
            final String value
    ) {
        this.headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public void setBody(
            final String body,
            final String contentType
    ) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
        setHeader("Content-Type", contentType);
    }

    public void setBody(
            final byte[] body,
            final String contentType
    ) {
        this.body = body;
        setHeader("Content-Type", contentType);
    }

    public byte[] getResponseBytes() {
        final var responseHeaders = new HashMap<>(this.headers);
        responseHeaders.put("Content-Length", List.of(String.valueOf(this.body.length)));
        try (var outputStream = new ByteArrayOutputStream()) {
            outputStream.write(statusLine.toLineString().getBytes(StandardCharsets.UTF_8));
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            for (var headerEntry : responseHeaders.entrySet()) {
                for (var value : headerEntry.getValue()) {
                    outputStream.write((headerEntry.getKey() + ": " + value + "\r\n")
                            .getBytes(StandardCharsets.UTF_8));
                }
            }
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(this.body);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
