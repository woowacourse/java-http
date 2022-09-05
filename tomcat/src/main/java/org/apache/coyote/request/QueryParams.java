package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> value;

    private QueryParams(final Map<String, String> value) {
        this.value = value;
    }

    public static QueryParams from(final String parameter) {
        if (parameter == null) {
            return new QueryParams(new HashMap<>());
        }
        Map<String, String> queryParams = new HashMap<>();
        String[] tokens = parameter.split(PARAMETER_SEPARATOR);
        for (String token : tokens) {
            String[] entry = token.split(KEY_VALUE_SEPARATOR);
            queryParams.put(entry[KEY_INDEX], entry[VALUE_INDEX]);
        }
        return new QueryParams(queryParams);
    }

    public Map<String, String> getValue() {
        return value;
    }
}
