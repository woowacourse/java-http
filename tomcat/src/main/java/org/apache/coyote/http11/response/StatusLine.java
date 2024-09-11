package org.apache.coyote.http11.response;

import org.apache.coyote.http11.component.HttpStatus;

public class StatusLine {

    private final String httpVersion;
    private HttpStatus httpStatus;

    public StatusLine(String httpVersion) {
        this(httpVersion, HttpStatus.OK);
    }

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

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
