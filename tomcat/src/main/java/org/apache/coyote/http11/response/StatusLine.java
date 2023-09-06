package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatusCode;

public class StatusLine {

    private final HttpStatusCode httpStatusCode;

    public StatusLine(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + httpStatusCode.getCode() + " " + httpStatusCode.getMessage() + " ";
    }
}
