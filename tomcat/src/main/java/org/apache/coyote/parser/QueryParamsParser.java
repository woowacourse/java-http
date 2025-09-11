package org.apache.coyote.parser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QueryParamsParser {

    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    public static Map<String, String> parse(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return queryParams;
        }

        String[] queries = queryString.split(PARAM_DELIMITER);
        for (String query : queries) {
            String[] paramPair = query.split(KEY_VALUE_DELIMITER);
            if (paramPair.length == 2) {
                String key = URLDecoder.decode(paramPair[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(paramPair[1], StandardCharsets.UTF_8);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }
}
