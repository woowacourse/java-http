package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestCookie {

    private final Map<String, String> cookies;

    public RequestCookie(final Map<String, String> cookies) {
        Objects.requireNonNull(cookies, "cookies must not be null");
        this.cookies = Map.copyOf(cookies);
    }

    public static RequestCookie from(final String cookieHeader) {
        final Map<String, String> parsed = new HashMap<>();

        if (cookieHeader != null && !cookieHeader.isBlank()) {
            final var parts = cookieHeader.split(";");
            for (final var part : parts) {
                final var keyValue = part.trim().split("=", 2);
                if (keyValue.length == 2) {
                    parsed.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        return new RequestCookie(parsed);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
