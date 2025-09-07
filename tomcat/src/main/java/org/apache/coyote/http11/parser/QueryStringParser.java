package org.apache.coyote.http11.parser;

import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryStringParser {

    private static final String AMPERSAND = "&";
    private static final String EQUAL = "=";
    private static final String EMPTY = "";

    private QueryStringParser() {
    }

    public static Map<String, String> parse(final String queryString) {
        final Map<String, String> query = new LinkedHashMap<>();
        final String[] pairs = queryString.split(AMPERSAND);

        for (final String pair : pairs) {
            final String[] keyValue = pair.split(EQUAL, 2);
            if (keyValue.length == 2) {
                query.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                query.put(keyValue[0], EMPTY);
            }
        }

        return query;
    }
}
