package org.apache.coyote.http11;

public class StatusLine {
    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    public StatusLine(HttpVersion httpVersion, StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public static StatusLine ok(String httpVersion) {
        return new StatusLine(HttpVersion.from(httpVersion), StatusCode.OK);
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode;
    }
}

