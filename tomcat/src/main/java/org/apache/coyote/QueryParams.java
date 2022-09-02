package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.exception.QueryStringFormatException;

public class QueryParams {

    public static final String QUERY_PARAMS_DELIMITER = "&";
    public static final String KEY_AND_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final int QUERY_PARAMS_FORMAT_SIZE = 2;
    private final Map<String, String> params;

    private QueryParams(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParams parseQueryParams(final String queryString) {
        if (queryString.isBlank()) {
            return new QueryParams(new HashMap<>());
        }
        String[] queries = queryString.split(QUERY_PARAMS_DELIMITER);
        Map<String, String> params = new HashMap<>();
        for (String query : queries) {
            String[] keyAndValue = query.split(KEY_AND_VALUE_DELIMITER);
            validateFormat(keyAndValue);
            params.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new QueryParams(params);
    }

    private static void validateFormat(final String[] keyAndValue) {
        if (keyAndValue.length != QUERY_PARAMS_FORMAT_SIZE) {
            throw new QueryStringFormatException();
        }
    }

    public String get(final String key) {
        return params.get(key);
    }

    public boolean exists() {
        return params.size() > 0;
    }
}
