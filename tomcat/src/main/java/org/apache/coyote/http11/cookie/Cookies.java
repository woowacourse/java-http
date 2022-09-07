package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = "; ";

    private final Map<String, String> cookies;

    public Cookies() {
        cookies = new HashMap<>();
    }

    public Cookies(final Map<String, String> cookies) {
        this();
        this.cookies.putAll(cookies);
    }

    public static Cookies from(final String cookieHeaderValue) {
        final String[] rawCookies = cookieHeaderValue.split(COOKIE_SEPARATOR);
        final Map<String, String> cookies = new HashMap<>();
        for (String rawCookie : rawCookies) {
            final int keyValueSeparatorIndex = rawCookie.indexOf(KEY_VALUE_SEPARATOR);
            cookies.put(
                    rawCookie.substring(0, keyValueSeparatorIndex),
                    rawCookie.substring(keyValueSeparatorIndex + 1)
            );
        }
        return new Cookies(cookies);
    }

    public static Cookies empty() {
        return new Cookies(new HashMap<>());
    }

    public String toHeaderValueString() {
        return cookies.keySet()
                .stream()
                .map(key -> key + KEY_VALUE_SEPARATOR + cookies.get(key))
                .collect(Collectors.joining(COOKIE_SEPARATOR));
    }

    public Optional<String> getValue(final String cookieKey) {
        return Optional.ofNullable(cookies.get(cookieKey));
    }

    public void addCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public void addSession(final String jSessionId) {
        cookies.put(SESSION_KEY, jSessionId);
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(cookies.get(SESSION_KEY));
    }
}
