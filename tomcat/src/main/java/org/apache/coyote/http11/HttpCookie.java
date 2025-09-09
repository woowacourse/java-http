package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final String cookieHeader) {
        this.cookies = parse(cookieHeader);
    }

    private Map<String, String> parse(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return Collections.emptyMap();
        }
        final Map<String, String> cookies = new HashMap<>();
        final String[] cookiePairs = cookieHeader.split("; ");
        for (final String cookiePair : cookiePairs) {
            final String[] keyValue = cookiePair.split("=");
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            }
        }
        return Collections.unmodifiableMap(cookies);
    }

    public boolean hasCookie(final String name) {
        return cookies.containsKey(name);
    }
}
