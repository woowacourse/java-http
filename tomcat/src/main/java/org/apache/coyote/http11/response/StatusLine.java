package org.apache.coyote.http11.response;

public class StatusLine {

    private final String httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(String httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
