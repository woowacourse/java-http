package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final int DEFAULT_COOKIE_PAIR_SIZE = 2;

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String line) {
        final Map<String, String> cookies = new HashMap<>();
        final String[] cookiePairs = line.split("; ");

        for (final String cookie : cookiePairs) {
            addCookie(cookies, cookie);
        }

        return new HttpCookie(cookies);
    }

    private static void addCookie(final Map<String, String> cookies, final String cookie) {
        if (cookie == null || cookie.isBlank()) {
            return;
        }

        final String[] pair = cookie.split("=");
        if (pair.length != DEFAULT_COOKIE_PAIR_SIZE) {
            throw new IllegalArgumentException("올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
        }
        cookies.put(pair[0], pair[1].trim());
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public Map<String, String> getValues() {
        return values;
    }
}
