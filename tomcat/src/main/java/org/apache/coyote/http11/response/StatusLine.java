package org.apache.coyote.http11.response;

import org.apache.coyote.protocol.Protocol;

public class StatusLine {

    public static final StatusLine DEFAULT_STATUS_LINE = new StatusLine(StatusCode.OK);

    private final Protocol protocol;
    private final StatusCode statusCode;

    public StatusLine(final StatusCode statusCode) {
        this(Protocol.HTTP11, statusCode);
    }

    public StatusLine(final Protocol protocol, final StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String parseResponse() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(protocol.getValue())
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(" ");
        return stringBuilder.toString();
    }
}
