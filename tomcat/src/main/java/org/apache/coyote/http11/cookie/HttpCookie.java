package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    public static HttpCookie from(final String rawCookie) {
        if (rawCookie.isBlank()) {
            return new HttpCookie(new HashMap<>());
        }

        final String[] cookiePairs = rawCookie.split(COOKIE_SEPARATOR, 2);
        final Map<String, String> cookies = new HashMap<>();
        for (String cookiePair : cookiePairs) {
            final int separatorIndex = cookiePair.indexOf(KEY_VALUE_SEPARATOR);
            final String key = cookiePair.substring(0, separatorIndex);
            final String value = cookiePair.substring(separatorIndex + 1);
            cookies.put(key, value);
        }
        return new HttpCookie(cookies);
    }

    public Optional<String> get(final String key) {
        return Optional.ofNullable(cookies.get(key));
    }

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }
}
