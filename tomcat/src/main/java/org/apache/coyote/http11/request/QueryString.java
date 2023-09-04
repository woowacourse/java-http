package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private static final String QUERY_STRING_BEGIN = "?";
    private static final String SEPARATOR = "&";
    private static final String DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EMPTY = -1;

    private final Map<String, String> items = new HashMap<>();

    private QueryString() {
        this(Map.of());
    }

    public QueryString(final Map<String, String> items) {
        this.items.putAll(items);
    }

    public static QueryString from(final String uri) {
        int queryStringIndex = uri.indexOf(QUERY_STRING_BEGIN);
        if (queryStringIndex == EMPTY) {
            return new QueryString();
        }
        return new QueryString(parseQueryString(uri, queryStringIndex));
    }

    private static Map<String, String> parseQueryString(final String uri, final int queryStringIndex) {
        final String queryString = uri.substring(queryStringIndex + 1);
        return Arrays.stream(queryString.split(SEPARATOR))
                .map(query -> query.split(DELIMITER))
                .collect(toMap(query -> query[KEY_INDEX], query -> query[VALUE_INDEX]));
    }

    public String get(final String key) {
        return items.get(key);
    }

    public Map<String, String> getItems() {
        return items;
    }
}
