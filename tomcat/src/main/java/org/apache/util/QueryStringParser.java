package org.apache.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class QueryStringParser {

    private static final String QUERY_STRING_REGEX = "^([a-zA-Z0-9%._-]+=[a-zA-Z0-9%._-]+)(&([a-zA-Z0-9%._-]+=[a-zA-Z0-9%._-]+))*$";
    private static final Pattern QUERY_STRING_PATTERN = Pattern.compile(QUERY_STRING_REGEX);
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int PARAM_KEY_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    public static boolean isQueryString(String source) {
        if (source == null || source.isBlank()) {
            return false;
        }
        if (QUERY_STRING_PATTERN.matcher(source).matches()) {
            return true;
        }
        return false;
    }

    public static Map<String, List<String>> parseQueryString(String source) {
        Map<String, List<String>> queryStrings = new HashMap<>();

        for (String queryString : source.split(PARAM_DELIMITER)) {
            String[] query = queryString.split(KEY_VALUE_DELIMITER);
            queryStrings.computeIfAbsent(query[PARAM_KEY_INDEX], k -> new ArrayList<>())
                    .add(query[PARAM_VALUE_INDEX]);
        }
        return queryStrings;
    }
}
