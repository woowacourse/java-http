package org.apache.coyote.http11.reqeust;

public record HttpRequest(
        HttpMethod method,
        String uri,
        HttpProtocolVersion protocolVersion,
        HttpHeaders headers,
        String body
) {
}
