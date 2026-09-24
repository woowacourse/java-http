package org.apache.coyote.http11;

import java.util.*;
import java.util.stream.Stream;

public class Cookies {
    private static final String PAIR_DELIMITER = ";";

    private final Map<String, Cookie> cookies;

    private Cookies(Map<String, Cookie> cookies) {
        this.cookies = Collections.unmodifiableMap(new LinkedHashMap<>(cookies));
    }

    public static Cookies of(Cookie... cookies) {
        return toCookies(List.of(cookies));
    }

    private static Cookies toCookies(List<Cookie> cookiePairs) {
        Map<String, Cookie> cookies = new LinkedHashMap<>();
        for (Cookie cookie : cookiePairs) {
            cookies.put(cookie.name(), cookie);
        }
        return new Cookies(cookies);
    }

    public static Cookies from(String cookieHeaderValue) {
        if (cookieHeaderValue == null || cookieHeaderValue.isBlank()) {
            return empty();
        }

        List<Cookie> cookiePairs = Stream.of(cookieHeaderValue.split(PAIR_DELIMITER))
                .map(Cookie::fromCookiePair)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return toCookies(cookiePairs);
    }

    public static Cookies empty() {
        return new Cookies(Map.of());
    }

    public Optional<Cookie> find(String name) {
        return Optional.ofNullable(cookies.get(name));
    }

    public Collection<Cookie> values() {
        return cookies.values();
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public Cookies add(Cookie cookie) {
        Map<String, Cookie> newCookies = new LinkedHashMap<>(cookies);
        newCookies.put(cookie.name(), cookie);
        return new Cookies(newCookies);
    }
}
