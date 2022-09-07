package org.apache.coyote.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StringParser {

    private static final String ELEMENT_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private StringParser() {
    }

    public static Map<String, String> toMap(final String source) {
        return Arrays.stream(source.split(ELEMENT_DELIMITER))
                .map(it -> it.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> b));
    }
}
