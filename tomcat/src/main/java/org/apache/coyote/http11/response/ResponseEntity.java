package org.apache.coyote.http11.response;

import org.apache.coyote.http11.auth.Cookie;
import org.apache.coyote.http11.request.Page;
import org.apache.coyote.http11.request.line.Protocol;

public class ResponseEntity {

    private final Protocol protocol;
    private final HttpStatus httpStatus;
    private final Cookie cookie;
    private final Location location;

    public ResponseEntity(Protocol protocol, HttpStatus httpStatus, Cookie cookie, Location location) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.cookie = cookie;
        this.location = location;
    }

    public static ResponseEntity getCookieNullResponseEntity(
            final Protocol protocol,
            final Page page
    ) {
        return new ResponseEntity(protocol, page.statusCode(), Cookie.from(null), Location.from(page.redirectUrl()));
    }

    public static ResponseEntity getCookieNullResponseEntity(
            final Protocol protocol,
            final HttpStatus status,
            final Location location
    ) {
        return new ResponseEntity(protocol, status, Cookie.from(null), location);
    }

    public String getProtocol() {
        return protocol.protocol();
    }

    public String getLocation() {
        return location.location();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Cookie getHttpCookie() {
        return cookie;
    }

}
