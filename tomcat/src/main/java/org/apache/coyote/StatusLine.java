package org.apache.coyote;

import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.StatusCode;

public class StatusLine {

    private HttpVersion httpVersion;
    private StatusCode statusCode;

    public StatusLine() {
        this.httpVersion = HttpVersion.HTTP11;
        this.statusCode = StatusCode.OK;
    }

    public void addHttpVersion(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void addStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusLineMessage() {
        return httpVersion.getValue() + " " + statusCode.getCode() + " " + statusCode.getMessage();
    }
}
