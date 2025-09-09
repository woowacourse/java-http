package org.apache.catalina.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Cookie {

    private final Map<String, String> values;

    private Cookie(Map<String, String> values) {
        this.values = values;
    }

    public static Cookie parse(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return new Cookie(Map.of());
        }
        Map<String, String> map = Arrays.stream(headerValue.split(";"))
                .map(String::trim)
                .map(kv -> kv.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));
        return new Cookie(map);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
