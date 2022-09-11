package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";

    private final String protocolVersion;
    private final int statusCode;
    private final String statusMessage;

    private StatusLine(final String protocolVersion, final int statusCode, final String statusMessage) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static StatusLine from(final StatusCode statusCode) {
        return new StatusLine(DEFAULT_PROTOCOL_VERSION, statusCode.getValue(), statusCode.getReasonPhrase());
    }

    @Override
    public String toString() {
        return String.join(" ", protocolVersion, String.valueOf(statusCode), statusMessage);
    }
}
