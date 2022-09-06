package org.apache.coyote.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    private static final String COOKIE_DELIMITER = "; ";

    private final List<Cookie> cookies;

    public Cookies() {
        this.cookies = new ArrayList<>();
    }

    public Cookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Cookies from(String cookie) {
        if (cookie == null) {
            return new Cookies();
        }

        final List<Cookie> cookies = Arrays.stream(cookie.split(COOKIE_DELIMITER))
                .map(Cookie::from)
                .collect(Collectors.toList());

        return new Cookies(cookies);
    }

    public Optional<Cookie> getCookie(String cookieKey) {
        return cookies.stream()
                .filter(it -> it.isSameKey(cookieKey))
                .findAny();
    }

    public Optional<Cookie> getJSessionCookie() {
        return cookies.stream()
                .filter(Cookie::isJSessionCookie)
                .findAny();
    }
}
