package org.apache.coyote.common.request.parser;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UriParser {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    public static Map<String, String> getQueryString(final URI uri) {
        final String rawQueryString = uri.getQuery();
        if (rawQueryString == null) {
            return new HashMap<>();
        }
        final String[] parsedQueryString = rawQueryString.split(QUERY_STRING_DELIMITER);
        return Arrays.stream(parsedQueryString)
                .map(qs -> qs.split(QUERY_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    public static String getPath(final URI uri) {
        return uri.getPath();
    }
}
