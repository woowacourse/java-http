package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_MAPPER = "=";
    private static final int INDEX_OF_QUERY_KEY = 0;
    private static final int INDEX_OF_QUERY_VALUE = 1;

    private QueryParser() {
    }

    public static Map<String, String> parse(String input) {
        Map<String, String> queryParam = new HashMap<>();

        for (String str : input.split(QUERY_PARAM_DELIMITER)) {
            String[] parsedQuery = str.split(QUERY_PARAM_MAPPER);
            queryParam.put(parsedQuery[INDEX_OF_QUERY_KEY], parsedQuery[INDEX_OF_QUERY_VALUE]);
        }

        return queryParam;
    }

}
