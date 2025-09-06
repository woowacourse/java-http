package org.apache.coyote.http11.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UriParser {

    private static final String QUERY_DELIMITER = "?";

    public static String parsePath(String uri) {
        int indexOfQueryDelimiter = uri.indexOf(QUERY_DELIMITER);
        if (indexOfQueryDelimiter == -1) {
            return uri;
        }
        return uri.substring(0, indexOfQueryDelimiter);
    }

    public static Map<String, String> parseQueryStrings(String uri) {
        int indexOfQueryDelimiter = uri.indexOf(QUERY_DELIMITER);
        return Arrays.stream(uri.substring(indexOfQueryDelimiter + 1).split("&"))
            .map(queryString -> queryString.split("="))
            .collect(Collectors.toUnmodifiableMap(
                strings -> strings[0], // key
                strings -> strings[1] // value
            ));
    }
}
