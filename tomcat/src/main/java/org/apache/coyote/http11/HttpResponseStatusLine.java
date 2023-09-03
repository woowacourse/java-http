package org.apache.coyote.http11;

public class HttpResponseStatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    final String httpVersion;
    final int statusCode;
    final String statusText;

    public HttpResponseStatusLine(final String httpVersion, final int statusCode, final String statusText) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public HttpResponseStatusLine(final int statusCode, final String statusText) {
        this.httpVersion = HTTP_VERSION;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static HttpResponseStatusLine OK() {
        return new HttpResponseStatusLine(200, "OK");
    }

    public static HttpResponseStatusLine UNAUTHORIZED() {
        return new HttpResponseStatusLine(401, "Unauthorized");
    }

    public static HttpResponseStatusLine FOUND() {
        return new HttpResponseStatusLine(302, "Found");
    }

    public static HttpResponseStatusLine BAD_REQUEST() {
        return new HttpResponseStatusLine(400, "Bad Request");
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + statusText;
    }
}
