package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class Parameters {

    private final Map<String, String> values;

    Parameters() {
        this("");
    }

    Parameters(final String parameters) {
        Objects.requireNonNull(parameters, "파라미터는 null일 수 없습니다.");
        this.values = parse(parameters);
    }

    String get(final String name) {
        return values.get(name);
    }

    private static Map<String, String> parse(final String raw) {
        if (raw.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parameters = new HashMap<>();
        for (String pair : raw.split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length == 2) {
                parameters.put(
                        decode(nameAndValue[0].strip()),
                        decode(nameAndValue[1].strip())
                );
            }
        }
        return Map.copyOf(parameters);
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
