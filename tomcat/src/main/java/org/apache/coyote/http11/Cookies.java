package org.apache.coyote.http11;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookies {
    private static final String COOKIE_DELIMITER = "; ";

    private final Map<String, Cookie> cookies;

    public Cookies(final Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Cookies parse(final String cookieString) {
        final String[] tokens = cookieString.split(COOKIE_DELIMITER);
        final Map<String, Cookie> cookies = Stream.of(tokens)
                .map(Cookie::of)
                .collect(Collectors.toMap(Cookie::getName, cookie -> cookie));
        return new Cookies(cookies);
    }

    public static Cookies empty() {
        return new Cookies(Map.of());
    }

    public Cookie get(final String key) {
        if (cookies.containsKey(key)) {
            return cookies.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 쿠키입니다.");
    }

    public Cookies add(final Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
        return this;
    }

    public boolean containsKey(final String key) {
        return cookies.containsKey(key);
    }
}
