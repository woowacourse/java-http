package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return new HttpCookie(cookies);
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] pair = cookie.split("=", 2);
            cookies.put(pair[0].trim(), pair[1].trim());
        }
        return new HttpCookie(cookies);
    }

    public Optional<String> getValue(final String name) {
        return Optional.ofNullable(values.get(name));
    }
}
