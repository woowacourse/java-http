package org.apache.coyote.http11.request;

import java.util.Objects;

public record RequestLine(
        String method,
        RequestTarget requestTarget,
        String httpVersion
) {
    public static RequestLine from(String line) {
        Objects.requireNonNull(line);
        String[] parts = line.split(" ", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("request format error");
        }
        return new RequestLine(parts[0], RequestTarget.from(parts[1]), parts[2]);
    }

    public String path() {
        Objects.requireNonNull(requestTarget);

        return requestTarget().path();
    }
}
