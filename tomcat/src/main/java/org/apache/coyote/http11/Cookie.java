package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookie {

    private final Map<String, String> values;

    private Cookie(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static Cookie from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new Cookie(Map.of());
        }

        Map<String, String> values = Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(cookie -> cookie.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(
                        Collectors.toMap(
                                parts -> parts[0].trim(),
                                parts -> parts[1].trim()
                        )
                );

        return new Cookie(values);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

}
