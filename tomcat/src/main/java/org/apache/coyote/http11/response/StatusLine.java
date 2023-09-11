package org.apache.coyote.http11.response;

import org.apache.coyote.protocol.Protocol;

public class StatusLine {

    private final Protocol protocol;
    private StatusCode statusCode;

    public StatusLine() {
        this(Protocol.HTTP11, StatusCode.OK);
    }

    public StatusLine(final Protocol protocol, final StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
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
