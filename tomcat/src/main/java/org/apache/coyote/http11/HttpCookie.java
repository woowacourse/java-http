package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = Map.copyOf(cookies);
    }

    public static HttpCookie from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(Collections.emptyMap());
        }

        Map<String, String> cookies = new HashMap<>();
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] tokens = pair.trim().split("=", 2);
            if (tokens.length == 2) {
                cookies.put(tokens[0].trim(), tokens[1].trim());
            }
        }
        return new HttpCookie(cookies);
    }

    public Optional<String> getValue(String name) {
        return Optional.ofNullable(cookies.get(name));
    }

    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }
}
