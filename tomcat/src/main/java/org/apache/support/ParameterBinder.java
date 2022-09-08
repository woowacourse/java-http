package org.apache.support;

import java.util.HashMap;
import java.util.Map;

public class ParameterBinder {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String PARAMETER_DELIMITER = "=";

    public static Map<String, String> bind(String query) {
        if (query == null || query.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> params = new HashMap<>();
        final String[] split = query.split(QUERY_STRING_SEPARATOR);

        for (String param : split) {
            String[] splitParam = param.split(PARAMETER_DELIMITER);
            final String key = splitParam[0];
            final String value = splitParam[1];

            params.put(key, value);
        }
        return params;
    }
}
