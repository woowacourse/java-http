package org.apache.coyote.common.request.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlParser {

    private static final int MIN_QUERY_STRING_DELIMITER_INDEX = 0;
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    public static Map<String, String> getQueryString(String url, int queryStringDelimiterIndex) {
        if (!hasQueryString(queryStringDelimiterIndex)) {
            return new HashMap<>();
        }
        final String rawQueryString = url.substring(queryStringDelimiterIndex + 1);
        final String[] parsedQueryString = rawQueryString.split(QUERY_STRING_DELIMITER);
        return Arrays.stream(parsedQueryString)
                .map(qs -> qs.split(QUERY_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    public static String getPath(String url, int queryStringDelimiterIndex) {
        if (!hasQueryString(queryStringDelimiterIndex)) {
            return url;
        }
        return url.substring(0, queryStringDelimiterIndex);
    }

    private static boolean hasQueryString(final int queryStringDelimiter) {
        return queryStringDelimiter >= MIN_QUERY_STRING_DELIMITER_INDEX;
    }
}
