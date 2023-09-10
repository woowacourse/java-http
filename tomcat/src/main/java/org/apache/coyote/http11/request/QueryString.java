package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_STRING_SEPARATOR = "=";

    private final Map<String, String> params;

    public static QueryString from(final String queryString) {
        if (queryString.isEmpty()) {
            return new QueryString(new HashMap<>());
        }
        final Map<String, String> params = Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(string -> Arrays.asList(string.split(QUERY_STRING_SEPARATOR)))
                .collect(Collectors.toMap(data -> data.get(0), data -> data.get(1), (a, b) -> b));
        return new QueryString(params);
    }

    private QueryString(final Map<String, String> params) {
        this.params = params;
    }

    public String get(final String key) {
        return params.get(key);
    }
}
