package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "=";
    private static final String COOKIES_DELIMITER = ";";

    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String rawValue) {
        final HashMap<String, String> values = new HashMap<>();
        if ("".equals(rawValue)) {
            return new HttpCookie(values);
        }
        final String[] cookies = rawValue.split(COOKIES_DELIMITER);

        for (final String cookie : cookies) {
            final String stripedCookie = cookie.strip();
            final String[] cookieKeyValue = stripedCookie.split(COOKIE_DELIMITER);

            values.put(cookieKeyValue[KEY], cookieKeyValue[VALUE]);
        }

        return new HttpCookie(values);
    }

    public void add(final String key, final String value) {
        values.put(key, value);
    }

    public String get(final String key) {
        return values.get(key);
    }

    public String getAllCookies() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + COOKIE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIES_DELIMITER));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
