package org.apache.coyote.http11;

import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;

public final class HttpCookies {

    private final List<HttpCookie> cookies;

    public HttpCookies(final List<HttpCookie> cookies) {
        this.cookies = List.copyOf(cookies);
    }

    public Optional<HttpCookie> getCookie(final String name) {
        return cookies.stream()
                .filter(it -> name.equals(it.getName()))
                .findFirst();
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }
}
