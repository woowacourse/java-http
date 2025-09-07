package org.apache.coyote.http11;

public record RequestLine(
        HttpMethod method,
        String uri,
        HttpVersion version
) {
}
