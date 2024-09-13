package org.apache.coyote.http11.response;

public class StatusLine {
    private String version;
    private String statusCode;
    private String statusMessage;

    public StatusLine() {
        this.version = "";
        this.statusCode = "";
        this.statusMessage = "";
    }

    public void setVersion(String version) {
        this.version = version;
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
