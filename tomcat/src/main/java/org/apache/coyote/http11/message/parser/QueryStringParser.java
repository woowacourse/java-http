package org.apache.coyote.http11.message.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class QueryStringParser {

    private static final String QUERY_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";
    private static final int PAIR = 2;

    private QueryStringParser() {
    }

    public static Map<String, String> parse(String queryString) {
        Map<String, String> queries = new HashMap<>();

        StringTokenizer tokenizer = new StringTokenizer(queryString, QUERY_DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            addQuery(tokenizer.nextToken(), queries);
        }
        return queries;
    }

    private static void addQuery(String query, Map<String, String> queries) {
        StringTokenizer tokenizer = new StringTokenizer(query, VALUE_DELIMITER);
        validateQuery(tokenizer);

        String key = tokenizer.nextToken();
        String value = tokenizer.nextToken();
        queries.put(key, value);
    }

    private static void validateQuery(StringTokenizer tokenizer) {
        if (tokenizer.countTokens() != PAIR) {
            System.out.println(tokenizer.countTokens());
            throw new IllegalArgumentException("쿼리 스트링 형식이 유효하지 않습니다.");
        }
    }
}
