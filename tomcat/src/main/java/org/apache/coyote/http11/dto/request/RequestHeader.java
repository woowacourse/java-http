package org.apache.coyote.http11.dto.request;

import static org.apache.coyote.http11.HttpConstants.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.HttpConstants.COOKIE_HEADER;
import static org.apache.coyote.http11.HttpConstants.EMPTY;

import java.util.Map;
import org.apache.coyote.http11.parser.request.CookieParser;

public record RequestHeader(
        Map<String, String> headers
) {

    public int getContentLength() {
        final String contentLength = headers.get(CONTENT_LENGTH_HEADER);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public boolean containsCookie(final String name) {
        if (headers.containsKey(COOKIE_HEADER)) {
            final String cookieString = headers.get(COOKIE_HEADER);
            if (cookieString != null) {
                final Cookie cookie = CookieParser.parse(cookieString);
                return cookie.containsCookie(name);
            }
        }
        return false;
    }

    public String getCookie(final String name) {
        if (containsCookie(name)) {
            final String cookieString = headers.get(COOKIE_HEADER);
            final Cookie cookie = CookieParser.parse(cookieString);
            return cookie.getCookie(name);
        }
        return EMPTY;
    }
}

