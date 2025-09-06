package org.apache.coyote.http11.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
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
        if (indexOfQueryDelimiter == -1) {
            return new HashMap<>();
        }
        return Arrays.stream(uri.substring(indexOfQueryDelimiter + 1).split("&"))
            .map(queryString -> queryString.split("="))
            .collect(Collectors.toMap(
                strings -> decodeValue(strings[0]), // key
                strings -> decodeValue(strings[1]), // value
                (oldValue, newValue) -> newValue
            ));
    }

    private static String decodeValue(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
