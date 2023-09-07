package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class QueryString {

    private static final String QUERY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private final HashMap<String, String> queries;

    private QueryString(final Map<String, String> queries) {
        this.queries = new HashMap<>(queries);
    }

    public static QueryString from(String queryString) {
        final String[] separateQuery = queryString.split(QUERY_SEPARATOR);
        final Map<String, String> queries = Arrays.stream(separateQuery)
                                                  .map(value -> value.split(KEY_VALUE_SEPARATOR))
                                                  .collect(toMap(value -> value[0], value -> value[1]));

        return new QueryString(queries);
    }

    public Map<String, String> getQueries() {
        return queries;
    }
}
