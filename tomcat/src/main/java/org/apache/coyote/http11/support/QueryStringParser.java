package org.apache.coyote.http11.support;

import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    private static final String REQUEST_PARAM_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";

    public static boolean hasQueryString(final String value) {
        return value.contains(REQUEST_PARAM_DELIMITER);
    }

    public static Map<String, String> parseQueryString(final String queryString) {
        final String[] queryPairs = queryString.split(REQUEST_PARAM_DELIMITER);

        final Map<String, String> queryParams = new HashMap<>();
        for (String queryPair : queryPairs) {
            final String[] values = queryPair.split(VALUE_DELIMITER);
            queryParams.put(values[0], values[1]);
        }
        return queryParams;
    }
}
