package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_CONNECTOR = "&";
    private static final String QUERY_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private QueryParams(final Map<String, String> values) {
        this.values = values;
    }

    public static QueryParams from(final String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (!queryString.isBlank()) {
            initQueryParams(queryParams, queryString);
        }
        return new QueryParams(queryParams);
    }

    private static void initQueryParams(final Map<String, String> queryParams, final String inputQueryString) {
        String[] queryStrings = inputQueryString.split(QUERY_CONNECTOR);
        for (String queryString : queryStrings) {
            String[] queryParam = queryString.split(QUERY_SEPARATOR);
            queryParams.put(queryParam[KEY_INDEX], queryParam[VALUE_INDEX]);
        }
    }

    public boolean isNotEmpty() {
        return !values.isEmpty();
    }

    public Map<String, String> getParams() {
        return values;
    }
}
