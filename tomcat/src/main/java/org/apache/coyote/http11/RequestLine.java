package org.apache.coyote.http11;

import java.io.IOException;

public record RequestLine(String method, RequestUri uri, String version) {
    public static RequestLine from(final String value) throws IOException {
        if (value == null || value.isBlank()) {
            throw new IOException("Request line is required");
        }

        final var parts = value.trim().split("\\s+", 3);
        if (parts.length != 3) {
            throw new IOException("Invalid request line");
        }

        return new RequestLine(parts[0], RequestUri.from(parts[1]), parts[2]);
    }
}
