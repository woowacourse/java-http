package org.apache.coyote.http11.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    public static final String SESSION_KEY = "JSESSIONID";

    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = "; ";

    private final List<Cookie> cookies;

    public Cookies() {
        cookies = new ArrayList<>();
    }

    public Cookies(final List<Cookie> cookies) {
        this();
        this.cookies.addAll(cookies);
    }

    public static Cookies from(final String cookieHeaderValue) {
        final String[] rawCookies = cookieHeaderValue.split(COOKIE_SEPARATOR);
        final List<Cookie> cookies = Arrays.stream(rawCookies)
                .map(rawCookie -> {
                    final int keyValueSeparatorIndex = rawCookie.indexOf(KEY_VALUE_SEPARATOR);
                    final String cookieKey = rawCookie.substring(0, keyValueSeparatorIndex);
                    final String cookieValue = rawCookie.substring(keyValueSeparatorIndex + 1);
                    return new Cookie(cookieKey, cookieValue);
                })
                .collect(Collectors.toList());
        return new Cookies(cookies);
    }

    public static Cookies empty() {
        return new Cookies(new ArrayList<>());
    }

    public Optional<String> getValue(final String cookieKey) {
        return cookies.stream()
                .filter(cookie -> cookie.getKey().equals(cookieKey))
                .findAny()
                .map(Cookie::getValue);
    }

    public Optional<String> getSessionId() {
        return cookies.stream()
                .filter(cookie -> cookie.getKey().equals(SESSION_KEY))
                .findAny()
                .map(Cookie::getValue);
    }
}
