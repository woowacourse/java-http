package org.apache.coyote.http11.response;

public class StatusLine {

    private final String version;
    private final int statusCode;
    private final String statusMessage;

    public StatusLine(String version, int statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
