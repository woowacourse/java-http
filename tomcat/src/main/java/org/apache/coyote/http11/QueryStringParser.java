package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    private static final String DEFAULT_URL_QUERY_SEPARATOR = "?";
    private static final String QUERY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    public static Map<String, String> parseUri(String uri) {
        Map<String, String> queryValues = new HashMap<>();
        int index = uri.indexOf(DEFAULT_URL_QUERY_SEPARATOR);
        String queryString = uri.substring(index + 1);

        String[] queries = queryString.split(QUERY_SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(KEY_VALUE_SEPARATOR);
            queryValues.put(split[KEY], split[VALUE]);
        }
        return queryValues;
    }
}
