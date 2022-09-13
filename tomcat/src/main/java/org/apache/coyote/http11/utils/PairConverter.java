package org.apache.coyote.http11.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PairConverter {

    private static final int KEY = 0;
    private static final int VALUE = 1;

    public static Map<String, String> toMap(
            final String target,
            final String pairDelimiter,
            final String keyValueDelimiter
    ) {
        if (target.isBlank()) {
            return Map.of("", "");
        }
        Map<String, String> parameters = new LinkedHashMap<>();
        for (String keyValue : target.split(pairDelimiter)) {
            String key = keyValue.split(keyValueDelimiter)[KEY].trim();
            String value = keyValue.split(keyValueDelimiter)[VALUE].trim();
            parameters.put(key, value);
        }
        return parameters;
    }
}
