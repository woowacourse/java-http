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

    public static Cookies of(Cookie... cookies) {
        return new Cookies(cookies);
    }


    public static Cookies empty() {
        return new Cookies();
    }

    public static Cookies fromHeaderValue(String headerValue) {
        final Cookies cookies = new Cookies();
        final String[] cookiePairs = headerValue.split("; ");

        for (String cookiePair : cookiePairs) {
            final String[] parts = cookiePair.split("=", 2);

            if (parts.length == 2) {
                cookies.cookies.add(Cookie.create(parts[0], parts[1]));
            }
        }
        return cookies;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append(cookie.toString()).append("; ");
        }
        return sb.toString();
    }

    public Cookies with(Cookie... cookies) {
        Cookies newCookies = new Cookies();
        newCookies.cookies.addAll(this.cookies);
        newCookies.cookies.addAll(Arrays.asList(cookies));
        return newCookies;

    }
}
