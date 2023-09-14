package org.apache.coyote.http11.response;

import org.apache.coyote.http11.auth.Cookie;
import org.apache.coyote.http11.request.line.Protocol;

public class HttpResponse {

    private final Protocol protocol;
    private final HttpStatus httpStatus;
    private final Cookie cookie;
    private final Location location;

    private HttpResponse(Protocol protocol, HttpStatus httpStatus, Cookie cookie, Location location) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.cookie = cookie;
        this.location = location;
    }

    public static HttpResponse getCookieNullResponseEntity(
            final Protocol protocol,
            final ResponsePage responsePage
    ) {
        return new HttpResponse(protocol, responsePage.statusCode(), Cookie.from(null), Location.from(responsePage.redirectUrl()));
    }

    public static HttpResponse getResponseEntity(
            final Protocol protocol,
            final Cookie cookie,
            final ResponsePage responsePage
    ) {
        System.out.println("cookie.get(\"JSESSIONID\") = " + cookie.get("JSESSIONID"));
        return new HttpResponse(protocol, responsePage.statusCode(), cookie, Location.from(responsePage.redirectUrl()));
    }

    public static HttpResponse getCookieNullResponseEntity(
            final Protocol protocol,
            final HttpStatus status,
            final Location location
    ) {
        return new HttpResponse(protocol, status, Cookie.from(null), location);
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
