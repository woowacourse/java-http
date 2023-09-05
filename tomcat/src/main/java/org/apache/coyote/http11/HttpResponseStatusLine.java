package org.apache.coyote.http11;

public class HttpResponseStatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private final HttpStatusCode httpStatus;

    public HttpResponseStatusLine(final HttpStatusCode httpStatus) {
        this.httpVersion = HTTP_VERSION;
        this.httpStatus = httpStatus;
    }

    public static HttpResponseStatusLine OK() {
        return new HttpResponseStatusLine(HttpStatusCode.OK);
    }

    public static HttpResponseStatusLine UNAUTHORIZED() {
        return new HttpResponseStatusLine(HttpStatusCode.UNAUTHORIZED);
    }

    public static HttpResponseStatusLine FOUND() {
        return new HttpResponseStatusLine(HttpStatusCode.FOUND);
    }

    public static HttpResponseStatusLine BAD_REQUEST() {
        return new HttpResponseStatusLine(HttpStatusCode.BAD_REQUEST);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return httpVersion + " " + httpStatus.getStatusCode() + " " + httpStatus.getStatusText();
    }
}
