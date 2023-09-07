package org.apache.coyote.http11.response;

import org.apache.coyote.http11.auth.Cookie;
import org.apache.coyote.http11.request.line.Protocol;

import static org.apache.coyote.http11.auth.Cookie.createSessionCookie;

public class ResponseEntity {

    private final Protocol protocol;
    private final HttpStatus httpStatus;
    private final String uri;
    private final Cookie cookie;

    public ResponseEntity(Protocol protocol, HttpStatus httpStatus, String uri, Cookie cookie) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.uri = uri;
        this.cookie = cookie;
    }

    public static ResponseEntity getCookieNullResponseEntity(final Protocol protocol, final HttpStatus httpStatus, final String uri) {
        return new ResponseEntity(protocol, httpStatus, uri, Cookie.from(null));
    }

    public static ResponseEntity of(final Protocol protocol, final HttpStatus httpStatus, final String uri) {
        return new ResponseEntity(protocol, httpStatus, uri, createSessionCookie());
    }

    public Protocol protocol() {
        return protocol;
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
