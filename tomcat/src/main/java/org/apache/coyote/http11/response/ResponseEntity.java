package org.apache.coyote.http11.response;

import org.apache.coyote.http11.auth.Cookie;

import static org.apache.coyote.http11.auth.Cookie.createSessionCookie;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String uri;
    private final Cookie cookie;

    private ResponseEntity(final HttpStatus httpStatus, final String uri) {
        this(httpStatus, uri, Cookie.from(null));
    }

    public static ResponseEntity getCookieResponseEntity(final HttpStatus httpStatus, final String uri) {
        return new ResponseEntity(httpStatus, uri, createSessionCookie());
    }

    public ResponseEntity(final HttpStatus httpStatus, final String uri, final Cookie cookie) {
        this.httpStatus = httpStatus;
        this.uri = uri;
        this.cookie = cookie;
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
