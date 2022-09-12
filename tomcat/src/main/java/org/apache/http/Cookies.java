package org.apache.http;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    private static final String COOKIES_DELIMITER_REGEX = "; ";

    private final List<Cookie> cookies;

    public Cookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Cookies parse(String cookieLine) {
        List<String> dividedCookies = List.of(cookieLine.split(COOKIES_DELIMITER_REGEX));
        List<Cookie> cookies = dividedCookies.stream()
            .map(Cookie::from)
            .collect(Collectors.toList());
        return new Cookies(cookies);
    }

    public static Cookies fromJSessionId(String id) {
        return new Cookies(List.of(Cookie.fromByJSessionId(id)));
    }

    public static Cookies empty() {
        return new Cookies(List.of());
    }

    public Optional<String> findJSessionId() {
        return cookies.stream()
            .filter(Cookie::isJSessionCookie)
            .findFirst()
            .map(Cookie::getValue);
    }
}
