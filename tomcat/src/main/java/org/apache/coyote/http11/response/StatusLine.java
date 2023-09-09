package org.apache.coyote.http11.response;

public class StatusLine {

    private final String httpVersion;
    private final String statusCode;
    private final String statusMessage;

    public StatusLine(String httpVersion, String statusCode, String statusMessage) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
