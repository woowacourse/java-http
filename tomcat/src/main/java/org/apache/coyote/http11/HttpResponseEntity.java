package org.apache.coyote.http11;

import java.util.HashMap;

public class HttpResponseEntity {

    private final HttpStatus httpStatus;
    private final String path;
    private HttpCookie httpCookie;
    private boolean isRedirect;

    public HttpResponseEntity(final HttpStatus httpStatus, final String path) {
        this.httpStatus = httpStatus;
        this.path = path;
        this.httpCookie = new HttpCookie(new HashMap<>());
        this.isRedirect = false;
    }

    public HttpResponseEntity(final HttpStatus httpStatus, final String path, final boolean isRedirect) {
        this.httpStatus = httpStatus;
        this.path = path;
        this.httpCookie = new HttpCookie(new HashMap<>());
        this.isRedirect = isRedirect;
    }

    public void changeRedirect() {
        isRedirect = true;
    }

    public boolean isRedirect() {
        return isRedirect;
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
