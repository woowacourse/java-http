package org.apache.coyote.http11.response;

public class StatusLine {

    private final String protocolVersion;
    private final int statusCode;
    private final String statusMessage;

    private StatusLine(final String protocolVersion, final int statusCode, final String statusMessage) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static StatusLine from(final String protocolVersion, final StatusCode statusCode) {
        return new StatusLine(protocolVersion, statusCode.getValue(), statusCode.getReasonPhrase());
    }

    @Override
    public String toString() {
        return String.join(" ", protocolVersion, String.valueOf(statusCode), statusMessage);
    }
}
