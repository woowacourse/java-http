package org.apache.coyote.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StringParser {

    private StringParser() {
    }

    public static Map<String, String> split(final String source, final String fieldDelimiter,
                                            final String keyValueDelimiter) {
        return Arrays.stream(source.split(fieldDelimiter))
                .map(it -> it.split(keyValueDelimiter))
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> b));
    }
}
