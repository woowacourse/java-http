package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookies {

    private final Map<String, HttpCookie> cookies;

    public HttpCookies() {
        this.cookies = new HashMap<>();
    }

    public HttpCookies(Map<String, HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public Map<String, HttpCookie> getCookies() {
        return cookies;
    }

    public Optional<HttpCookie> get(String name) {
        return Optional.of(cookies.get(name));
    }
}
