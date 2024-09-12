package org.apache.coyote.http11.response;


import org.apache.coyote.http11.request.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private HttpStatus status;

    public StatusLine(HttpVersion httpVersion, HttpStatus status) {
        this.httpVersion = httpVersion;
        this.status = status;
    }

    public static StatusLine ok() {
        return new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
    }

    public String buildStatusLineResponse() {
        return String.format("%s %d %s ", httpVersion.getVersion(), status.getCode(), status.getMessage());
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.status = httpStatus;
    }
}
