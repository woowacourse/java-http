package org.apache.coyote.httprequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryString {

    private static final String DELIMITER = "&";
    private static final String KEY_VALUE_SPLITTER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> queries;

    private QueryString(final Map<String, String> queries) {
        this.queries = queries;
    }

    public static QueryString from(final String queryStrings) {
        final List<String> eachQueries = List.of(queryStrings.split(DELIMITER));
        final Map<String, String> queries = new HashMap<>();
        for (String query : eachQueries) {
            final List<String> keyAndValue = List.of(query.split(KEY_VALUE_SPLITTER));
            queries.put(keyAndValue.get(KEY_INDEX), keyAndValue.get(VALUE_INDEX));
        }
        return new QueryString(queries);
    }
}
