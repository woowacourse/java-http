package org.apache.coyote.http11;

public class StatusLine {

    private final String version;
    private final int statusCode;
    private final String statusMessage;

    public StatusLine(String version, Status status) {
        this.version = version;
        this.statusCode = status.getStatusCode();
        this.statusMessage = status.getStatusMessage();
    }

    public String serialize() {
        return version + " " + statusCode + " " + statusMessage + " ";
    }
}
