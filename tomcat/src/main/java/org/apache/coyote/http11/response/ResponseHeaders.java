package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;

public class ResponseHeaders {

    private final String contentType;
    private final String locationHeader;
    private final String setCookieHeader;

    private ResponseHeaders(String contentType, String locationHeader, String setCookieHeader) {
        this.contentType = contentType;
        this.locationHeader = locationHeader;
        this.setCookieHeader = setCookieHeader;
    }

    public static ResponseHeaders fromContentType(final String contentType) {
        return new ResponseHeaders(contentType, null, null);
    }

    public static ResponseHeaders fromResourcePath(final String resourcePath) {
        final String contentType = resourcePath.split("\\.")[1];
        return new ResponseHeaders(contentType, null, null);
    }

    public static ResponseHeaders withLocation(final String resourcePath, final String location) {
        final String contentType = resourcePath.split("\\.")[1];
        return new ResponseHeaders(contentType, location, null);
    }

    public static ResponseHeaders withLocationAndSetCookie(final String resourcePath, final String location,
                                                           final HttpCookie cookie, final String cookieName) {
        final String contentType = resourcePath.split("\\.")[1];
        return new ResponseHeaders(contentType, location, cookie.cookieToString(cookieName));
    }

    public String contentTypeToString() {
        return String.format("Content-Type: text/%s;charset=utf-8 ", contentType);
    }

    public String locationToString() {
        return String.format("Location: %s ", locationHeader);
    }

    public String setCookieToString() {
        return String.format("Set-Cookie: %s ", setCookieHeader);
    }

    public boolean hasSetCookieHeader() {
        return setCookieHeader != null;
    }
}
