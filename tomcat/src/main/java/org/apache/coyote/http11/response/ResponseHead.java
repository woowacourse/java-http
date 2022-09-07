package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;

public class ResponseHead {

    private final String contentType;
    private final String locationHeader;
    private final String setCookieHeader;

    private ResponseHead(String contentType, String locationHeader, String setCookieHeader) {
        this.contentType = contentType;
        this.locationHeader = locationHeader;
        this.setCookieHeader = setCookieHeader;
    }

    public static ResponseHead fromContentType(final String contentType) {
        return new ResponseHead(contentType, null, null);
    }

    public static ResponseHead fromResourcePath(final String resourcePath) {
        final String contentType = resourcePath.split("\\.")[1];
        return new ResponseHead(contentType, null, null);
    }

    public static ResponseHead withLocation(final String resourcePath, final String location) {
        final String contentType = resourcePath.split("\\.")[1];
        return new ResponseHead(contentType, location, null);
    }

    public static ResponseHead withLocationAndSetCookie(final String resourcePath, final String location,
                                                        final HttpCookie cookie, final String cookieName) {
        final String contentType = resourcePath.split("\\.")[1];
        return new ResponseHead(contentType, location, cookie.cookieToString(cookieName));
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
