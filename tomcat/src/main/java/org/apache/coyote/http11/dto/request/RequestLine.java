package org.apache.coyote.http11.dto.request;

public record RequestLine(
        String method,
        RequestPath path,
        String protocolVersion
) {
}
