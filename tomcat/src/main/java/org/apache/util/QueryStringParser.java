package org.apache.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_STRING_ELEMENT_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";

    public static Map<String, String> parseQueryString(final String value) {
        Map<String, String> queryString = Arrays.stream(
                value.split(QUERY_STRING_ELEMENT_DELIMITER)
        ).collect(Collectors.toMap(
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[0],
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[1]
        ));
        return queryString;
    }

    public static Map<String, String> parseUriQueryString(final String uri) {
        if (!haveQueryString(uri)) {
            return new HashMap<>();
        }
        Map<String, String> queryString = Arrays.stream(
                uri.split("\\" + QUERY_STRING_DELIMITER)[1]
                        .split(QUERY_STRING_ELEMENT_DELIMITER)
        ).collect(Collectors.toMap(
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[0],
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[1]
        ));
        return queryString;
    }

    private static boolean haveQueryString(final String uri) {
        return uri.contains(QUERY_STRING_DELIMITER);
    }
}
