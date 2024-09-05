package org.apache.coyote.http11;


public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private final HttpStatus status;

    public StatusLine(HttpStatus status) {
        this(HTTP_VERSION, status);
    }

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

    public String buildStatusLineResponse() {
        return String.format("%s %d %s ", httpVersion, status.getCode(), status.getMessage());
    }
}
