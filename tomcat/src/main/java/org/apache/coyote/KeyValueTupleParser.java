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
                .map(Tuple::of)
                .collect(toMap(tuple -> tuple.key, tuple -> tuple.value));
    }

    private static class Tuple {
        private static final int VALID_LENGTH = 2;
        private static final String EMPTY_DATA = "";

        private final String key;
        private final String value;

        private Tuple(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public static Tuple of(final String tupleString) {
            final String[] keyValue = tupleString.split(KEY_VALUE_DELIMITER);
            if (keyValue.length == VALID_LENGTH) {
                return new Tuple(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
            }
            return new Tuple(keyValue[KEY_INDEX], EMPTY_DATA);
        }
    }
}
