package org.apache.coyote.http11.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Cookie {

    private static final Cookie EMPTY = new Cookie(Map.of());

    private final Map<String, String> values;

    private Cookie(final Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static Cookie empty() {
        return EMPTY;
    }

    public static Cookie from(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return empty();
        }

        final Map<String, String> values = new HashMap<>();
        for (String token : cookieHeader.split(";")) {
            final int separatorIndex = token.indexOf('=');
            if (separatorIndex <= 0) {
                continue;
            }

            final String name = token.substring(0, separatorIndex).trim();
            final String value = token.substring(separatorIndex + 1).trim();
            if (!name.isEmpty()) {
                values.put(name, value);
            }
        }
        return new Cookie(values);
    }

    public static Cookie of(final String name, final String value) {
        return new Cookie(Map.of(
                Objects.requireNonNull(name),
                Objects.requireNonNull(value)
        ));
    }

    public Optional<String> get(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    public String toHeaderValue() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
