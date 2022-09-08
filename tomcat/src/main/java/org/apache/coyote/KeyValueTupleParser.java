package org.apache.coyote;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KeyValueTupleParser {
    private static final String TUPLE_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private KeyValueTupleParser() {
    }

    public static Map<String, String> parse(final String keyValues) {
        if (keyValues == null || keyValues.isEmpty()) {
            return new HashMap<>();
        }
        return Arrays.stream(keyValues.split(TUPLE_DELIMITER))
                .map(keyValue -> keyValue.split(KEY_VALUE_DELIMITER))
                .collect(toMap(keyValue -> keyValue[KEY_INDEX], keyValue -> keyValue[VALUE_INDEX]));
    }
}
