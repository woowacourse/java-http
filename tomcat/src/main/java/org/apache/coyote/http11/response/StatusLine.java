package org.apache.coyote.http11.response;

public class StatusLine {

    private final String version;
    private final String statusCode;
    private final String statusMessage;

    public StatusLine(String version, String statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusLine() {
        return String.join(" ", version, statusCode, statusMessage) + " ";
    }
}
