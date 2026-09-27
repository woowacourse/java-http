package org.apache.coyote.http11.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Cookies {
    private final List<Cookie> cookies = new ArrayList<>();

    private Cookies(Cookie... cookies) {
        this.cookies.addAll(Arrays.asList(cookies));
    }

    public static Cookies empty() {
        return new Cookies();
    }

    public static Cookies fromHeaderValue(String headerValue) {
        final Cookies cookies = new Cookies();
        final String[] cookiePairs = headerValue.split(";");

        for (String cookiePair : cookiePairs) {
            final String[] parts = cookiePair.split("=", 2);

            if (parts.length == 2) {
                cookies.cookies.add(Cookie.create(parts[0].trim(), parts[1].trim()));
            }
        }
        return cookies;
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public Optional<String> getValue(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public List<Cookie> values() {
        return List.copyOf(cookies);
    }
}
