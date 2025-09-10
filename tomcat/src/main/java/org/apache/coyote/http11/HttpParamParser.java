package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpParamParser {

    public static Map<String, String> parseKeyValuePairs(String input, String pairDelimiter) {
        Map<String, String> map = new HashMap<>();
        if (input == null || input.isBlank()) {
            return map;
        }
        for (String pair : input.split(pairDelimiter)) {
            String key = "";
            String value = "";
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length > 0 && keyValue[0] != null) {
                key = keyValue[0].trim();
            }
            if (keyValue.length == 2 && keyValue[1] != null) {
                value = keyValue[1].trim();
            }
            if (!key.isEmpty()) {
                map.put(key, value);
            }
        }
        return map;
    }
}
