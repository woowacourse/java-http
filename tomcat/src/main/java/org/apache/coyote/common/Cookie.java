package org.apache.coyote.common;

import java.util.Map;

public class Cookie {

    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie empty() {
        return new Cookie(Map.of());
    }

    @Override
    public String toString() {
        return "Cookie{" +
               "cookies=" + cookies +
               '}';
    }
}
