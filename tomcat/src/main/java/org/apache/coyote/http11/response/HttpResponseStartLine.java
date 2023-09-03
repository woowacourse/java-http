package org.apache.coyote.http11.response;

public class HttpResponseStartLine {

    private final String httpVersion;
    private final StatusCode statusCode;

    public HttpResponseStartLine(final String httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
