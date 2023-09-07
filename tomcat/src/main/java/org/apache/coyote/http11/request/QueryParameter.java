package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String PARAMETER_DELIMITER = "=";

    private final Map<String, String> params;

    private QueryParameter(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParameter from(final String queryString) {
        final Map<String, String> params = new HashMap<>();

        if (queryString != null) {
            final String[] queries = queryString.split(QUERY_STRING_SEPARATOR);

            Arrays.stream(queries)
                    .map(param -> param.split(PARAMETER_DELIMITER))
                    .forEach(splitParam -> {
                        final String key = splitParam[0];
                        final String value = splitParam[1];
                        params.put(key, value);
                    });
        }

        return new QueryParameter(params);
    }

    public static QueryParameter empty() {
        return new QueryParameter(new HashMap<>());
    }

    public Map<String, String> getParams() {
        return params;
    }
}
