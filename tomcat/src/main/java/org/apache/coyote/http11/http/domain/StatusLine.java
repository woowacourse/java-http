package org.apache.coyote.http11.http.domain;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    public StatusLine(final HttpVersion httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public String getStatusLine() {
        return httpVersion.getValue() + " " + statusCode.getStatusCode() + " ";
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
