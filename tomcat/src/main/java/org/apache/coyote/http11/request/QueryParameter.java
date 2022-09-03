package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String PARAMETER_DELIMITER = "=";

    private final Map<String, String> params;

    private QueryParameter(Map<String, String> params) {
        this.params = params;
    }

    public static QueryParameter from(String query) {
        if (query == null) {
            return new QueryParameter(new HashMap<>());
        }

        Map<String, String> params = new HashMap<>();
        final String[] split = query.split(QUERY_STRING_SEPARATOR);

        for (String param : split) {
            String[] splitParam = param.split(PARAMETER_DELIMITER);
            final String key = splitParam[0];
            final String value = splitParam[1];

            params.put(key, value);
        }

        return new QueryParameter(params);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
