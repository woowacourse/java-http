package org.apache.coyote.http11;

public record RequestLine(
        HttpMethod method,
        String path,
        HttpVersion version
) {
}
