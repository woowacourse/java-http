package org.apache.coyote.http11.response;

import org.apache.coyote.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    public StatusLine(final HttpVersion httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return statusCode.getReasonPhrase();
    }
}
