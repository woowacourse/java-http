package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.common.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;

    public StatusLine(HttpStatusCode httpStatusCode) {
        this(HttpVersion.HTTP_1_1, httpStatusCode);
    }

    public StatusLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return httpVersion.getExpression() + " " + httpStatusCode.getCode() + " " + httpStatusCode.getMessage() + " ";
    }
}
