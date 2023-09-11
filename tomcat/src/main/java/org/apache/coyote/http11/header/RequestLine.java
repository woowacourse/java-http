package org.apache.coyote.http11.header;

import java.net.URI;

public record RequestLine(
        HttpMethod method,
        URI uri,
        HttpVersion version
) {
    public static RequestLine parse(final String requestLine) {
        final var tokens = requestLine.split(" ");
        return new RequestLine(
                HttpMethod.of(tokens[0]),
                URI.create(tokens[1]),
                HttpVersion.of(tokens[2])
        );
    }
}
