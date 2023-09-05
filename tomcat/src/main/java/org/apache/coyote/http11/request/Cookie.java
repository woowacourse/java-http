package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
    private final Map<String, String> cookieData;

    private Cookie(final Map<String, String> cookieData) {
        this.cookieData = cookieData;
    }

    public static Cookie from(final String value) {
        if (value == null) {
            return new Cookie(Collections.emptyMap());
        }

        final String[] cookies = value.split("; ");
        final Map<String, String> cookieData = Arrays.stream(cookies)
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookiePair -> cookiePair[0], cookiePair -> cookiePair[1]));

        return new Cookie(cookieData);
    }

    public String get(final String key) {
        return cookieData.get(key);
    }
}
