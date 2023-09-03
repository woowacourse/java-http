package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpStatus;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String uri;
    private final Cookie cookie;

    public ResponseEntity(final HttpStatus httpStatus, final String uri) {
        this(httpStatus, uri, Cookie.from(null));
    }

    public ResponseEntity(final HttpStatus httpStatus, final String uri, final Cookie cookie) {
        this.httpStatus = httpStatus;
        this.uri = uri;
        this.cookie = cookie;
    }

    public void setCookie(final String key, final String value) {
        cookie.put(key, value);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getUri() {
        return uri;
    }

    public Cookie getHttpCookie() {
        return cookie;
    }
}
