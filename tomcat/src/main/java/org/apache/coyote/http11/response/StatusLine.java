package org.apache.coyote.http11.response;

import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.component.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    public StatusLine(HttpVersion httpVersion) {
        this(httpVersion, HttpStatus.OK);
    }

    public StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
