package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpStatusLine {

    private String httpVersion;
    private HttpStatus httpStatus;

    public HttpStatusLine(String httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public int getHttpStatusCode() {
        return this.httpStatus.getStatusCode();
    }

    public String getHttpStatusMessage() {
        return this.httpStatus.getStatusMessage();
    }
}
