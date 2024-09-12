package org.apache.catalina.response;

public class StatusLine {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private final Status status;

    public StatusLine(Status status) {
        this.status = status;
    }

    public String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public int getStatusCode() {
        return status.code();
    }

    public String getStatusMessage() {
        return status.name();
    }
}
