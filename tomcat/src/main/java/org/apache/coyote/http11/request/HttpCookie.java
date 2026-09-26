package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = Map.copyOf(cookies);
    }

    public Optional<String> getValue(String name) {
        return Optional.ofNullable(cookies.get(name));
    }
}
