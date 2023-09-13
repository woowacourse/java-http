package org.apache.coyote.http11.response;

public class StatusLine {

    private final HttpStatusCode httpStatusCode;

    public StatusLine(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
