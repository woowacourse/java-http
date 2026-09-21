package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class HttpCookies {

    private static final String COOKIE_DELIMITER = ";";
    private static final String NAME_VALUE_DELIMITER = "=";
    private static final int NAME_VALUE_PART_COUNT = 2;

    private final Map<String, String> values;

    private HttpCookies(final Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    static HttpCookies empty() {
        return new HttpCookies(Map.of());
    }

    static HttpCookies parse(final String headerValue) {
        final var values = Arrays.stream(headerValue.split(COOKIE_DELIMITER))
                .map(String::strip)
                .map(HttpCookies::parseNameValue)
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(
                        NameValue::name,
                        NameValue::value,
                        (previous, replacement) -> replacement));
        return new HttpCookies(values);
    }

    Optional<String> get(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    private static Optional<NameValue> parseNameValue(final String cookie) {
        final var parts = cookie.split(NAME_VALUE_DELIMITER, NAME_VALUE_PART_COUNT);
        if (parts.length != NAME_VALUE_PART_COUNT || parts[0].isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new NameValue(parts[0].strip(), parts[1].strip()));
    }

    private record NameValue(String name, String value) {
    }
}
