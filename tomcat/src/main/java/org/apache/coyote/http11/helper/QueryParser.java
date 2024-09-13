package org.apache.coyote.http11.helper;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private static final String DELIMITER_OF_QUERY = "&";
    private static final String DELIMITER_OF_KEY_VALUE = "=";
    private static final int INDEX_OF_QUERY_KEY = 0;
    private static final int INDEX_OF_QUERY_VALUE = 1;

    private static QueryParser INSTANCE;

    public static QueryParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QueryParser();
        }
        return INSTANCE;
    }

    private QueryParser() {
    }

    public Map<String, String> parse(String rawQuery) {
        validateNull(rawQuery);
        Map<String, String> result = new HashMap<>();
        String[] tokens = rawQuery.split(DELIMITER_OF_QUERY);
        for (String token : tokens) {
            validateQueryFormat(token);
            String key = token.split(DELIMITER_OF_KEY_VALUE)[INDEX_OF_QUERY_KEY];
            String value = token.split(DELIMITER_OF_KEY_VALUE)[INDEX_OF_QUERY_VALUE];
            result.put(key, value);
        }
        result.forEach(System.out::printf);
        return result;
    }

    private void validateNull(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 형태의 쿼리스트링입니다.");
        }
    }

    private void validateQueryFormat(String raw) {
        if (!raw.contains(DELIMITER_OF_KEY_VALUE)) {
            throw new IllegalArgumentException("유효하지 않은 형태의 쿼리스트링입니다.");
        }
    }
}
