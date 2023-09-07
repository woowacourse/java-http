package org.apache.coyote.http.response;

import org.apache.coyote.http.common.Protocol;

public class StatusLine {

    private final Protocol protocol;
    private final StatusCode statusCode;

    private StatusLine(final Protocol protocol, final StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
    }

    public static StatusLine from(final StatusCode statusCode) {
        return new StatusLine(Protocol.HTTP_1_1, statusCode);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
