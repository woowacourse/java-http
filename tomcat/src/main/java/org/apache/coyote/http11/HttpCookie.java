package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final String cookieHeader) {
        this.cookies = parse(cookieHeader);
    }

    private Map<String, String> parse(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parsedCookies = new HashMap<>();
        for (final String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.strip().split("=", 2);
            if (pair.length == 2) {
                parsedCookies.put(pair[0].strip(), pair[1].strip());
            }
        }
        return Map.copyOf(parsedCookies);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }
}
