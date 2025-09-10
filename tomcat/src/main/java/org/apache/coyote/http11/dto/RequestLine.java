package org.apache.coyote.http11.dto;

public record RequestLine(
        String method,
        String uri,
        String protocol
) {
}
