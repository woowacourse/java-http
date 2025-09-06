package org.apache.coyote.http11;

public record RequestLine(
        String method,
        String uri,
        String version
) {
}
