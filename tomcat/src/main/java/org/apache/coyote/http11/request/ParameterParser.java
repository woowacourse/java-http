package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class ParameterParser {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_SIZE = 2;

    private ParameterParser() {}

    public static Map<String, String> parse(final String source, final String delimiter) {
        if (source == null || source.isBlank()) {
            return Map.of();
        }
        final Map<String, String> parameters = new HashMap<>();
        for (final String token : source.split(delimiter)) {
            final String[] keyAndValue = token.trim().split(KEY_VALUE_DELIMITER, KEY_VALUE_SIZE);
            if (keyAndValue.length == KEY_VALUE_SIZE) {
                parameters.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return parameters;
    }
}
