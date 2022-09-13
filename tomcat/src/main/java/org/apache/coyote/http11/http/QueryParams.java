package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParams {

    private static final String QUERY_PARAMETER = "?";
    private static final String VALUE_DELIMITER = "&";
    private static final String KEY_VALUE = "=";
    public static final int KEY = 0;
    public static final int VALUE = 1;

    private final Map<String, String> values;

    private QueryParams(final Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static QueryParams of(final String uri) {
        String queryString = uri.substring(uri.indexOf(QUERY_PARAMETER) + 1);
        return new QueryParams(toMap(queryString.split(VALUE_DELIMITER)));
    }

    private static Map<String, String> toMap(final String[] queries) {
        return Arrays.stream(queries)
                .map(query -> query.split(KEY_VALUE))
                .collect(Collectors.toMap(split -> split[KEY], split -> split[VALUE], (a, b) -> b));
    }

    public String find(final String key) {
        return values.get(key);
    }
}
