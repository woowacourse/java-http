package org.apache.coyote.http11;

public class StatusLine {

    private final String protocolVersion;
    private final Status status;

    public StatusLine(String protocolVersion, Status status) {
        this.protocolVersion = protocolVersion;
        this.status = status;
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getStatusMessage() {
        return status.getMessage();
    }

    public String build() {
        return protocolVersion + " " + getStatusCode() + " " + getStatusMessage();
    }

    @Override
    public String toString() {
        return build();
    }
}
