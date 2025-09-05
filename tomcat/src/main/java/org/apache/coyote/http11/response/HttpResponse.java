package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;
import org.apache.coyote.http11.response.util.HttpResponseFormatter;

public record HttpResponse(
        HttpProtocolVersion protocolVersion,
        HttpStatus status,
        HttpHeaders headers,
        String body
) {

    HttpResponse(
            HttpProtocolVersion protocolVersion,
            HttpStatus status,
            HttpHeaders headers
    ) {
        this(protocolVersion, status, headers, null);
    }

    public byte[] getBytes() {
        final HttpResponseFormatter formatter = HttpResponseFormatter.getInstance();

        return formatter.format(this).getBytes();
    }
}
