package org.apache.coyote.http11.response;

public class HttpResponseStartLine {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private final String httpVersion;
    private final StatusCode statusCode;

    private HttpResponseStartLine(final String httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public static HttpResponseStartLine of(final StatusCode statusCode) {
        return new HttpResponseStartLine(HTTP_VERSION, statusCode);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
