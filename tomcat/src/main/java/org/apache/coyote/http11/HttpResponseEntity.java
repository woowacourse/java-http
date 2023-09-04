package org.apache.coyote.http11;

public class HttpResponseEntity {

    private final HttpStatus httpStatus;
    private final String path;
    private HttpCookie httpCookie;

    public HttpResponseEntity(final HttpStatus httpStatus, final String path) {
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public boolean hasCookie() {
        return httpCookie != null;
    }

    public void addCookie(final HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

}
