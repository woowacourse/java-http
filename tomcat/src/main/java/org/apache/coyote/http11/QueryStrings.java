package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private static final String QUERY_PARAMETER = "?";
    private static final String VALUE_DELIMITER = "&";
    private static final String KEY_VALUE = "=";

    private final Map<String, String> values = new HashMap<>();

    public QueryStrings(final String uri) {
        String queryString = uri.substring(uri.indexOf(QUERY_PARAMETER) + 1);
        String[] queries = queryString.split(VALUE_DELIMITER);
        for (String query : queries) {
            String[] split = query.split(KEY_VALUE);
            values.put(split[0], split[1]);
        }
    }

    public String find(final String key) {
        return values.get(key);
    }
}
