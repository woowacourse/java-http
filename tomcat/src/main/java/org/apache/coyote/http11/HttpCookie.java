package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(String cookieHeader) {
        Map<String, String> values = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(values);
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.split("=", 2);

            if (parts.length == 2) {
                values.put(parts[0].trim(), parts[1].trim());
            }
        }
        
        return new HttpCookie(values);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }
}
