package org.apache.coyote.response;

import org.apache.coyote.StatusCode;

public class StatusLine {

    private final String protocolVersion;
    private final StatusCode statusCode;

    private StatusLine(final String protocolVersion, final StatusCode statusCode) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
    }

    public static StatusLine from(final StatusCode statusCode) {
        return new StatusLine("HTTP/1.1", statusCode);
    }

    @Override
    public String toString() {
        return new StringBuilder(protocolVersion)
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getReasonPhrase())
                .append("\r\n")
                .toString();
    }
}
