package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class HttpCookie {
    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(String headerValue) {
        for (String cookie : headerValue.split(";")) {
            String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2) {
                values.put(pair[0].trim(), pair[1].trim());
            }
        }
    }

    public Optional<String> findValue(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
