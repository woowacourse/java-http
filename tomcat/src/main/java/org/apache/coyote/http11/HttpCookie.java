package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(final String cookieHeader) {
        this.cookies = parse(cookieHeader);
    }

    private Map<String, String> parse(final String cookieHeader) {
        if (cookieHeader == null) {
            return Map.of();
        }

        final Map<String, String> cookies = new HashMap<>();
        for (final String pair : cookieHeader.split(";")) {
            final String[] keyValue = pair.split("=", 2);

            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return cookies;
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
