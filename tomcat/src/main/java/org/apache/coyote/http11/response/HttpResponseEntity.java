package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;

public class HttpResponseEntity {
    private final String path;
    private final HttpCookie cookie;
    private final HttpStatusCode httpStatusCode;

    public HttpResponseEntity(final String path, final HttpCookie cookie, final HttpStatusCode httpStatusCode) {
        this.path = path;
        this.cookie = cookie;
        this.httpStatusCode = httpStatusCode;
    }

    public String getPath() {
        return path;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
