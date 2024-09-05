package org.apache.coyote.common;

import java.util.Map;
import java.util.Optional;

public class Cookie {

    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie empty() {
        return new Cookie(Map.of());
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(cookies.get(key));
    }

    @Override
    public String toString() {
        return "Cookie{" +
               "cookies=" + cookies +
               '}';
    }
}
