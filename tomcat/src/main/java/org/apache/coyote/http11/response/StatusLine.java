package org.apache.coyote.http11.response;

public class StatusLine {
    private static final String VERSION = "HTTP/1.1";

    private final String version;
    private String statusCode;
    private String statusMessage;

    public StatusLine() {
        this.version = VERSION;
        this.statusCode = "";
        this.statusMessage = "";
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
