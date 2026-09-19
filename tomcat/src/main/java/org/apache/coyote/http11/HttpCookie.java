package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HttpCookie(Map<String, String> values) {
    public HttpCookie {
        values = Map.copyOf(values);
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(Map.of());
        }

        final var parsedValues = new HashMap<String, String>();

        for (final var cookie : cookieHeader.split(";")) {
            final var trimmedCookie = cookie.trim();
            final var separatorIndex = trimmedCookie.indexOf('=');

            if (separatorIndex <= 0) {
                continue;
            }

            final var name = trimmedCookie.substring(0, separatorIndex);
            final var value = trimmedCookie.substring(separatorIndex + 1);

            parsedValues.put(name, value);
        }

        return new HttpCookie(parsedValues);
    }

    public Optional<String> value(final String name) {
        return Optional.ofNullable(values.get(name));
    }
}
