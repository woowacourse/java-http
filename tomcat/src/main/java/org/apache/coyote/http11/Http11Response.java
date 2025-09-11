package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Response {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    public Http11Response(
            final int statusCode,
            final String contentType,
            final String body
    ) {
        this(statusCode, Map.of("Content-Type", List.of(contentType)), body.getBytes(StandardCharsets.UTF_8));
    }

    public Http11Response(
            final int statusCode,
            final String contentType,
            final byte[] body
    ) {
        this(statusCode, Map.of("Content-Type", List.of(contentType)), body);
    }

    public Http11Response(
            final int statusCode,
            final Map<String, List<String>> headers,
            final byte[] body
    ) {
        this.statusCode = statusCode;
        this.headers = new HashMap<>(headers);
        this.body = body;
    }

    public static Http11Response redirect(final String location) {
        return new Http11Response(302, Map.of("Location", List.of(location)), new byte[0]);
    }

    public byte[] getResponseBytes() {
        final String statusText = getStatusText(this.statusCode);
        final String responseLine = "HTTP/1.1 " + this.statusCode + " " + statusText;
        final var responseHeaders = new HashMap<>(this.headers);
        responseHeaders.put("Content-Length", List.of(String.valueOf(this.body.length)));
        try (final var outputStream = new ByteArrayOutputStream()) {
            outputStream.write(responseLine.getBytes(StandardCharsets.UTF_8));
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            for (final var headerEntry : responseHeaders.entrySet()) {
                final String headerKey = headerEntry.getKey();
                for (final String headerValue : headerEntry.getValue()) {
                    final String headerLine = headerKey + ": " + headerValue;
                    outputStream.write(headerLine.getBytes(StandardCharsets.UTF_8));
                    outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
                }
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
