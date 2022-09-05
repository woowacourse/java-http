package org.apache.coyote.http11.response;

public class StatusLine {

    private String protocolVersion;
    private String statusCode;
    private String statusMessage;

    public StatusLine(String protocolVersion, String statusCode, String statusMessage) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
