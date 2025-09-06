package org.apache.coyote.http11.reqeust;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;

public record HttpRequest(
        HttpMethod method,
        String uri,
        QueryParameters queryParameters,
        HttpProtocolVersion protocolVersion,
        HttpHeaders headers,
        String body
) {
}
