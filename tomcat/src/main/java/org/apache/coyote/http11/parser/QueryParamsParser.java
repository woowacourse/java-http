package org.apache.coyote.http11.parser;

import java.util.HashMap;
import java.util.Map;

public class QueryParamsParser {

    public static final String PARAM_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";

    public static Map<String, String> parse(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return queryParams;
        }

        String[] queries = queryString.split(PARAM_DELIMITER);
        for (String query : queries) {
            String[] paramPair = query.split(KEY_VALUE_DELIMITER);
            if (paramPair.length == 2) {
                queryParams.put(paramPair[0], paramPair[1]);
            }
        }
        return queryParams;
    }
}
