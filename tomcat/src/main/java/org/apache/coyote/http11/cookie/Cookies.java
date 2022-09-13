package org.apache.coyote.http11.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    public static final String SESSION_KEY = "JSESSIONID";
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
                .map(Cookie::from)
                .collect(Collectors.toList());
        return new Cookies(cookies);
    }

    public static Cookies empty() {
        return new Cookies(new ArrayList<>());
    }

    public Optional<String> getSessionId() {
        return getValue(SESSION_KEY);
    }

    public Optional<String> getValue(final String cookieKey) {
        return cookies.stream()
                .filter(cookie -> cookie.getKey().equals(cookieKey))
                .findAny()
                .map(Cookie::getValue);
    }
}
