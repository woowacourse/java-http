package org.apache.coyote.http11.request;

public record RequestLine(
        String method,
        String requestUri
) {
}
