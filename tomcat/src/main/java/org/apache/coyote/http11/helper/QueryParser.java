package org.apache.coyote.http11.helper;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private static final String DELIMITER_OF_QUERY = "&";
    private static final String DELIMITER_OF_KEY_VALUE = "=";

    public Map<String, String> parse(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        String[] tokens = rawQuery.split(DELIMITER_OF_QUERY);
        for (String token : tokens) {
            String key = token.split(DELIMITER_OF_KEY_VALUE)[0];
            String value = token.split(DELIMITER_OF_KEY_VALUE)[1];
            result.put(key, value);
        }
        return result;
    }
}
