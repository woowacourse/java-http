package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(Map.of());
        }

        final Map<String, String> cookies = new HashMap<>();

        for (final String cookie : cookieHeader.split(";")) {
            final String[] cookieKeyValue = cookie.trim().split("=", 2);

            validateCookie(cookieKeyValue, cookie);

            final String cookieName = cookieKeyValue[0];
            final String cookieValue = cookieKeyValue[1];

            cookies.put(cookieName, cookieValue);
        }

        return new HttpCookie(Map.copyOf(cookies));
    }

    private static void validateCookie(final String[] cookieKeyValue, final String cookie) {
        if (cookieKeyValue.length != 2) {
            throw new IllegalArgumentException("Invalid cookie: " + cookie);
        }
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(cookies.get(key));
    }
}
