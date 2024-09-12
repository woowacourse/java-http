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
            validateQueryFormat(token);
            String key = token.split(DELIMITER_OF_KEY_VALUE)[0];
            String value = token.split(DELIMITER_OF_KEY_VALUE)[1];
            result.put(key, value);
        }
        return result;
    }

    private void validateQueryFormat(String raw) {
        if (!raw.contains(DELIMITER_OF_KEY_VALUE)) {
            throw new IllegalArgumentException("유효하지 않은 형태의 쿼리스트링입니다.");
        }
    }
}
