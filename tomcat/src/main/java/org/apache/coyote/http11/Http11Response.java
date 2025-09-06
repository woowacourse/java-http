package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public final class Http11Response {

    private final int statusCode;
    private final String contentType;
    private final byte[] body;

    public Http11Response(
            final int statusCode,
            final String contentType,
            final String body
    ) {
        this(statusCode, contentType, body.getBytes(StandardCharsets.UTF_8));
    }

    public Http11Response(
            final int statusCode,
            final String contentType,
            final byte[] body
    ) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public byte[] getResponseBytes() {
        final String statusText = getStatusText(this.statusCode);
        final String responseHeaders = """
                HTTP/1.1 %d %s
                Content-Type: %s
                Content-Length: %d
                
                """.formatted(this.statusCode, statusText, this.contentType, this.body.length);
        byte[] headers = responseHeaders.getBytes(StandardCharsets.UTF_8);
        byte[] fullResponse = new byte[headers.length + this.body.length];
        System.arraycopy(headers, 0, fullResponse, 0, headers.length);
        System.arraycopy(this.body, 0, fullResponse, headers.length, this.body.length);
        return fullResponse;
    }

    private String getStatusText(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            default -> "OK";
        };
    }
}
