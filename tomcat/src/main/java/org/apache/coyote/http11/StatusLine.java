package org.apache.coyote.http11;

public class StatusLine {
    private static final String SPACE = " ";
    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    private StatusLine(HttpVersion httpVersion, StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public static StatusLine of(HttpVersion httpVersion, int statusCode) {
        return new StatusLine(httpVersion, StatusCode.valueOf(statusCode));
    }

    public String getStatusLine() {
        return httpVersion.getVersion() + SPACE + statusCode.getStatusCode() + SPACE;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "StatusLine{" +
                "httpVersion=" + httpVersion +
                ", statusCode=" + statusCode +
                '}';
    }
}
