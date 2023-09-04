package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {
    private static final Cookies EMPTY = new Cookies(Collections.emptyList());

    private final List<Cookie> cookies;

    private Cookies(final List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Cookies from(final String cookies) {
        if (cookies == null || cookies.isBlank()) {
            return EMPTY;
        }
        final List<Cookie> result = Arrays.stream(cookies.split(" "))
                .map(Cookie::from)
                .collect(Collectors.toList());
        return new Cookies(result);
    }

    public Optional<Cookie> getCookieOf(final String key) {
        return cookies.stream()
                .filter(cookie -> cookie.getKey().equals(key))
                .findFirst();
    }
}
