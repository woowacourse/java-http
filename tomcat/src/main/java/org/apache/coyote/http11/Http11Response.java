package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Http11Response {

    private final int statusCode;
    private final Map<String, String> headers;
    private final byte[] body;

    public Http11Response(
            final int statusCode,
            final String contentType,
            final String body
    ) {
        this(statusCode, Map.of("Content-Type", contentType), body.getBytes(StandardCharsets.UTF_8));
    }

    public Http11Response(
            final int statusCode,
            final String contentType,
            final byte[] body
    ) {
        this(statusCode, Map.of("Content-Type", contentType), body);
    }

    public Http11Response(
            final int statusCode,
            final Map<String, String> headers,
            final byte[] body
    ) {
        this.statusCode = statusCode;
        this.headers = new HashMap<>(headers);
        this.body = body;
    }

    public static Http11Response redirect(final String location) {
        return new Http11Response(302, Map.of("Location", location), new byte[0]);
    }

    public void addCookie(
            final String name,
            final String value
    ) {
        final String cookieValue = String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax", name, value);
        headers.put("Set-Cookie", cookieValue);
    }

    public byte[] getResponseBytes() {
        final String statusText = getStatusText(this.statusCode);
        final String responseLine = "HTTP/1.1 " + this.statusCode + " " + statusText;
        final var responseHeaders = new HashMap<>(this.headers);
        responseHeaders.put("Content-Length", String.valueOf(this.body.length));
        try (final var outputStream = new ByteArrayOutputStream()) {
            outputStream.write(responseLine.getBytes(StandardCharsets.UTF_8));
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            for (final var header : responseHeaders.entrySet()) {
                final String headerLine = header.getKey() + ": " + header.getValue();
                outputStream.write(headerLine.getBytes(StandardCharsets.UTF_8));
                outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(this.body);
            return outputStream.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStatusText(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 302 -> "Found";
            case 404 -> "Not Found";
            default -> "OK";
        };
    }
}
