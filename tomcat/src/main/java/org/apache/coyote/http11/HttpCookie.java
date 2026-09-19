package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookieHeader) {
        final Map<String, String> values = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(values);
        }

        for (String cookie : cookieHeader.split(";")) {
            final String trimmedCookie = cookie.trim();
            final int delimiterIndex = trimmedCookie.indexOf("=");

            if (delimiterIndex <= 0) {
                continue;
            }

            final String name = trimmedCookie.substring(0, delimiterIndex).trim();
            final String value = trimmedCookie.substring(delimiterIndex + 1).trim();
            values.put(name, value);
        }

        return new HttpCookie(values);
    }

    public Optional<String> getValue(final String name) {
        return Optional.ofNullable(values.get(name));
    }
}