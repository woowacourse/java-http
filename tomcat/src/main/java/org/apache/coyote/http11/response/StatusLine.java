package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;

public class StatusLine {

    private static final String SPACE_SEPARATOR = " ";

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    private StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine of11(HttpStatus httpStatus) {
        return new StatusLine(HttpVersion.HTTP11, httpStatus);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getContents() {
        return httpVersion.getHttpVersion() + SPACE_SEPARATOR + httpStatus.getCode() + SPACE_SEPARATOR + httpStatus.getMessage();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
