package org.apache.coyote.http11.response;

public class StatusLine {

    private String protocolVersion;
    private String statusCode;
    private String statusMessage;

    public StatusLine(final String protocolVersion, final String statusCode, final String statusMessage) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getValue() {
        return protocolVersion + " " + statusCode + " " + statusMessage + " ";
    }
}
