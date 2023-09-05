package org.apache.coyote.http11.common;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getCookieValue(final String cookieKey) {
        return cookies.get(cookieKey);
    }
}
