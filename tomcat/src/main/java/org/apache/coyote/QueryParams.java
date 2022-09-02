package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    public static final String QUERY_PARAMS_DELIMITER = "&";
    public static final String KEY_AND_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private final Map<String, String> params;

    private QueryParams(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParams parseQueryParams(final String queryString) {
        String[] queries = queryString.split(QUERY_PARAMS_DELIMITER);
        Map<String, String> params = new HashMap<>();
        for (String query : queries) {
            String[] keyAndValue = query.split(KEY_AND_VALUE_DELIMITER);
            params.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new QueryParams(params);
    }

    public String get(final String key) {
        return params.get(key);
    }
}
