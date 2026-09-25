package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static HttpCookie from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(Map.of());
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

        return new HttpCookie(values);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }

}
