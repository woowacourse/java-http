package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

final class UrlEncodedParameters {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";
    private static final int NAME_VALUE_PART_COUNT = 2;

    private final Map<String, String> values;

    private UrlEncodedParameters(final Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    static Optional<UrlEncodedParameters> parse(final String encodedParameters) {
        if (encodedParameters.isEmpty()) {
            return Optional.of(new UrlEncodedParameters(Map.of()));
        }

        final var nameValues = Arrays.stream(encodedParameters.split(PARAMETER_DELIMITER))
                .map(UrlEncodedParameters::decodeNameValue)
                .toList();
        if (nameValues.stream().anyMatch(Optional::isEmpty)) {
            return Optional.empty();
        }

        final var values = nameValues.stream()
                .map(Optional::orElseThrow)
                .collect(Collectors.toMap(
                        NameValue::name,
                        NameValue::value,
                        (previous, replacement) -> replacement));
        return Optional.of(new UrlEncodedParameters(values));
    }

    Optional<String> get(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    private static Optional<NameValue> decodeNameValue(final String encodedNameValue) {
        final var parts = encodedNameValue.split(NAME_VALUE_DELIMITER, NAME_VALUE_PART_COUNT);
        if (parts.length != NAME_VALUE_PART_COUNT) {
            return Optional.empty();
        }

        try {
            return Optional.of(new NameValue(
                    URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(parts[1], StandardCharsets.UTF_8)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private record NameValue(String name, String value) {
    }
}
