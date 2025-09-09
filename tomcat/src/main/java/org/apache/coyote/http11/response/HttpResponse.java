package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;

public record HttpResponse(
        HttpProtocolVersion protocolVersion,
        HttpStatus status,
        HttpHeaders headers,
        String body
) {

    public HttpResponse(
            HttpProtocolVersion protocolVersion,
            HttpStatus status,
            HttpHeaders headers
    ) {
        this(protocolVersion, status, headers, null);
    }

    public HttpResponse(
            HttpProtocolVersion protocolVersion,
            HttpStatus status
    ) {
        this(protocolVersion, status, new HttpHeaders(), null);
    }
}
