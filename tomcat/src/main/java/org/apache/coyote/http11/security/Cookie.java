package org.apache.coyote.http11.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private final Map<String, String> cookieMap;

    private Cookie(final Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public static Cookie from(final String rawCookie) {
        if (rawCookie == null) {
            return new Cookie(Collections.emptyMap());
        }

        String[] cookies = rawCookie.split("; ");

        Map<String, String> cookieMap = Arrays.stream(cookies)
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookiePair -> cookiePair[0], cookiePair -> cookiePair[1]));

        return new Cookie(cookieMap);
    }

    public boolean hasNotKey(final String key) {
        return !cookieMap.containsKey(key);
    }

    public String getCookieValue(final String cookieKey) {
        return cookieMap.get(cookieKey);
    }
}
