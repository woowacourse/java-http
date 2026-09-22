package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final List<String> cookieHeaders) {
        if (cookieHeaders == null) {
            return;
        }

        parseCookies(cookieHeaders);
    }

    private void parseCookies(final List<String> cookieHeaders) {
        for (String cookieHeader : cookieHeaders) {
            parseCookieHeader(cookieHeader);
        }
    }

    private void parseCookieHeader(final String cookieHeader) {
        for (String cookie : cookieHeader.split(";")) {
            parseCookie(cookie);
        }
    }

    private void parseCookie(final String cookie) {
        final String[] cookieParts = cookie.split("=", 2);

        if (cookieParts.length != 2) {
            return;
        }

        final String name = cookieParts[0].trim();
        final String value = cookieParts[1].trim();

        cookies.put(name, value);
    }

    public String get(final String name) {
        return cookies.get(name);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }
}
