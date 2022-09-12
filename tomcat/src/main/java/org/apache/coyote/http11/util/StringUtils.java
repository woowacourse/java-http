package org.apache.coyote.http11.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";

    public static Map<String, String> parseKeyAndValues(String input) {
        Map<String, String> result = new HashMap<>();
        String[] keyAndValues = input.split(QUERY_PARAM_DELIMITER);
        for (String keyAndValue : keyAndValues) {
            String[] keyValue = keyAndValue.split(KEY_AND_VALUE_DELIMITER);
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1];
            result.put(key, value);
        }
        return result;
    }
}
