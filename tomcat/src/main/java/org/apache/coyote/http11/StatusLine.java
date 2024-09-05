package org.apache.coyote.http11;


public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private HttpStatus status;

    public StatusLine() {
        this(HTTP_VERSION, HttpStatus.OK);
    }

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

    public void setHttpStatus(HttpStatus httpStatus) {
        this.status = httpStatus;
    }
}
