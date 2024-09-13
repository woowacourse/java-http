package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MapFactory {
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY_IDX = 0;
    private static final int VALUE_IDX = 1;

    public static Map<String, String> create(final String body, final String elementSeparator, final String keyValueSeparator) {
        return Arrays.stream(body.split(elementSeparator))
                .map(element -> Arrays.asList(element.split(keyValueSeparator)))
                .filter(keyValue -> keyValue.size() == KEY_VALUE_SIZE)
                .filter(keyValue -> !keyValue.get(KEY_IDX).isBlank() && !keyValue.get(VALUE_IDX).isBlank())
                .collect(Collectors.toMap(
                        keyValue -> keyValue.get(KEY_IDX).trim(),
                        keyValue -> keyValue.get(VALUE_IDX).trim()
                ));
    }
}
