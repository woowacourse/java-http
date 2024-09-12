package org.apache.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_VALUE_COUNT = 2;
    private static final int KEY_ORDER = 0;
    private static final int VALUE_ORDER = 1;

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public HttpCookie(String key, String value) {
        this.cookie = new HashMap<>(Map.of(key, value));
    }

    public static HttpCookie from(String cookie) {
        return new HttpCookie(parseCookie(cookie));
    }

    private static Map<String, String> parseCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            return new HashMap<>();
        }

        return Arrays.stream(cookie.split(COOKIE_SEPARATOR))
                .filter(pair -> pair.contains(KEY_VALUE_SEPARATOR))
                .map(pair -> pair.split(KEY_VALUE_SEPARATOR, KEY_VALUE_COUNT))
                .filter(pair -> pair.length == KEY_VALUE_COUNT)
                .collect(Collectors.toConcurrentMap(
                        pair -> pair[KEY_ORDER].trim(),
                        pair -> pair[VALUE_ORDER].trim(),
                        (v1, v2) -> v1));
    }

    public HttpCookie add(String key, String value) {
        cookie.put(key, value);
        return this;
    }

    public String getValue(String key) {
        return cookie.get(key);
    }

    @Override
    public String toString() {
        return cookie.entrySet()
                .stream()
                .map(entry -> entry.getKey() + KEY_VALUE_SEPARATOR + entry.getValue())
                .collect(Collectors.joining(COOKIE_SEPARATOR));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(cookie, that.cookie);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cookie);
    }
}
