package org.apache.coyote.http11;


public class StatusLine {

    private final String httpVersion;
    private final HttpStatus status;

    public StatusLine(String httpVersion, HttpStatus status) {
        this.httpVersion = httpVersion;
        this.status = status;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getStatusMessage() {
        return status.getMessage();
    }
}
