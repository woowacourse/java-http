package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private HttpStatusCode httpStatusCode;

    public StatusLine() {
        this(HttpVersion.HTTP_1_1, HttpStatusCode.OK);
    }

    public StatusLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return httpVersion.getExpression() + " " + httpStatusCode.getCode() + " " + httpStatusCode.getMessage() + " ";
    }
}
