package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

class Parameters {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private Parameters(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    static Parameters from(String raw) {
        if (raw == null) {
            return new Parameters(Map.of());
        }

        Map<String, String> values = Arrays.stream(raw.split(PAIR_DELIMITER))
                .map(pair -> pair.split(KEY_VALUE_DELIMITER, 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1],
                        (previous, current) -> current));
        return new Parameters(values);
    }

    String get(String name) {
        return values.get(name);
    }
}
