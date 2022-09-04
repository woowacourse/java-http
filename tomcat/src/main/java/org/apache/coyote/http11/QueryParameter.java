package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryParameter {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public QueryParameter(Map<String, String> values) {
        this.values = values;
    }

    public static QueryParameter from(String queryString) {
        String[] queryParameters = queryString.split(QUERY_STRING_DELIMITER);
        Map<String, String> params = initializeParams(queryParameters);
        return new QueryParameter(params);
    }

    private static Map<String, String> initializeParams(String[] queryParameters) {
        Map<String, String> params = new ConcurrentHashMap<>();
        for (String queryParameter : queryParameters) {
            String[] keyAndValue = queryParameter.split(KEY_VALUE_DELIMITER);
            params.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return params;
    }

    public Map<String, String> getValues() {
        return values;
    }
}
