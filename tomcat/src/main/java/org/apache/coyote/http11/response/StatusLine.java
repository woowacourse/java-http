package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public HttpVersion httpVersion() {
        return this.httpVersion;
    }

    public HttpStatus httpStatus() {
        return this.httpStatus;
    }
}
