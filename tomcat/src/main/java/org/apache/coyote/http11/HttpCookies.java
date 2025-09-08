package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private final Map<String, HttpCookie> cookies = new HashMap<>();

    public HttpCookies(final String cookieHeaderValue) {
        if (cookieHeaderValue == null || cookieHeaderValue.isBlank()) {
            return;
        }
        final String[] cookieTokens = cookieHeaderValue.split("; ");
        for (final String token : cookieTokens) {
            final String[] keyValue = token.split("=");
            if (keyValue.length == 2) {
                final String key = keyValue[0];
                final String value = keyValue[1];
                final HttpCookie cookie = new HttpCookie(key, value);
                cookies.put(key, cookie);
            }
        }
    }

    public boolean hasCookie(final String name) {
        return cookies.containsKey(name);
    }

    public HttpCookie getCookie(final String name) {
        return cookies.get(name);
    }
}
