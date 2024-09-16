package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpStatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private String httpVersion;
    private HttpStatus httpStatus;

    public HttpStatusLine() {
        this.httpVersion = HTTP_VERSION;
        this.httpStatus = HttpStatus.OK;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
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
