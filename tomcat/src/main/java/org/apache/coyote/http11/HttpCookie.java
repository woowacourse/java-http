package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        for (String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2) {
                cookies.put(pair[0].trim(), pair[1].trim());
            }
        }
    }

    public String get(final String name) {
        return cookies.get(name);
    }

    public String put(final String key, final String value) {
        return cookies.put(key, value);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }

    @Override
    public String toString() {
        return cookies.toString();
    }
}
