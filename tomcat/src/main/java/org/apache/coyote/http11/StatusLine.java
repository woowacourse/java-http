package org.apache.coyote.http11;

public class StatusLine {

    private final String protocolVersion;
    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(final String protocolVersion, final HttpStatus status) {
        this.protocolVersion = protocolVersion;
        this.statusCode = status.getCode();
        this.reasonPhrase = status.getReasonPhrase();
    }

    public StatusLine(final String protocolVersion, final int statusCode, final String reasonPhrase) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String format() {
        return protocolVersion + " " + statusCode + " " + reasonPhrase + " ";
    }
}
