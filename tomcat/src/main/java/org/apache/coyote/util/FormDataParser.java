package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public class FormDataParser {

    public static Map<String, String> parseFormData(final String queryString) {
        Map<String, String> params = new HashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue[1];
            params.put(key, value);
        }
        return params;
    }
}
