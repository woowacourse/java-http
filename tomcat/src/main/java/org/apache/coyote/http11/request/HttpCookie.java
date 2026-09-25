package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String NAME_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String header) {
        final Map<String, String> cookies = new HashMap<>();
        if (header == null || header.isBlank()) {
            return new HttpCookie(cookies);
        }
        for (final String pair : header.split(COOKIE_SEPARATOR)) {
            putCookie(cookies, pair.trim());
        }
        return new HttpCookie(cookies);
    }

    private static void putCookie(final Map<String, String> cookies, final String pair) {
        final int idx = pair.indexOf(NAME_VALUE_SEPARATOR);
        if (idx <= 0) {
            return;
        }
        cookies.put(pair.substring(0, idx), pair.substring(idx + 1));
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
