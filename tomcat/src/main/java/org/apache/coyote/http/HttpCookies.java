package org.apache.coyote.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookies {

    private static final String COOKIE_PAIR_DELIMITER = ";";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final int EXPECTED_COOKIE_PARTS = 2;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, HttpCookie> cookies = new HashMap<>();

    public HttpCookies(final String cookieLine) {
        Optional.ofNullable(cookieLine)
                .ifPresent(this::parseCookies);
    }

    private void parseCookies(final String cookieLine) {
        Arrays.stream(cookieLine.split(COOKIE_PAIR_DELIMITER))
                .map(this::splitCookiePair)
                .filter(parts -> parts.length == EXPECTED_COOKIE_PARTS)
                .forEach(this::addCookie);
    }

    private String[] splitCookiePair(final String cookiePair) {
        return cookiePair.split(COOKIE_KEY_VALUE_DELIMITER, EXPECTED_COOKIE_PARTS);
    }

    private void addCookie(final String[] parts) {
        final String name = parts[COOKIE_KEY_INDEX].trim();
        final String value = parts[COOKIE_VALUE_INDEX].trim();
        cookies.put(name, new HttpCookie(name, value));
    }

    public void add(final HttpCookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public HttpCookie get(final String name) {
        return cookies.get(name);
    }
}
