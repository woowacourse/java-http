package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String PAIR_DELIMITER = ";";
    private static final String VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String rawCookie) {
        if (rawCookie == null || rawCookie.isBlank()) {
            return new HttpCookie(Map.of());
        }
        final Map<String, String> values = Arrays.stream(rawCookie.split(PAIR_DELIMITER))
                .map(String::trim)
                .map(pair -> pair.split(VALUE_DELIMITER, 2))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),
                        pair -> pair[1].trim(),
                        (first, second) -> first
                ));
        return new HttpCookie(values);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(values.get(key));
    }
}
