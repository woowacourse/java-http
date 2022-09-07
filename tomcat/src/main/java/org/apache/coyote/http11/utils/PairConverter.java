package org.apache.coyote.http11.utils;

import java.util.HashMap;
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
        Map<String, String> parameters = new HashMap<>();
        for (String keyValue : target.split(pairDelimiter)) {
            String key = keyValue.split(keyValueDelimiter)[KEY];
            String value = keyValue.split(keyValueDelimiter)[VALUE];
            parameters.put(key, value);
        }
        return parameters;
    }
}
