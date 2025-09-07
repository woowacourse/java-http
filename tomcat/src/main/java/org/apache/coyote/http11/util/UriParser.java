package org.apache.coyote.http11.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.httpRequest.QueryStrings;

public class UriParser {

    private static final String QUERY_DELIMITER = "?";

    public static String parsePath(String uri) {
        int indexOfQueryDelimiter = uri.indexOf(QUERY_DELIMITER);
        if (indexOfQueryDelimiter == -1) {
            return uri;
        }
        return uri.substring(0, indexOfQueryDelimiter);
    }

    public static QueryStrings parseQueryStrings(String uri) {
        int indexOfQueryDelimiter = uri.indexOf(QUERY_DELIMITER);
        if (indexOfQueryDelimiter == -1) {
            return new QueryStrings(new HashMap<>());
        }
        Map<String, String> queryStrings = Arrays.stream(uri.substring(indexOfQueryDelimiter + 1).split("&"))
            .map(queryString -> queryString.split("="))
            .collect(Collectors.toMap(
                strings -> decodeValue(strings[0]), // key
                strings -> decodeValue(strings[1]), // value
                (oldValue, newValue) -> newValue
            ));
        return new QueryStrings(queryStrings);
    }

    private static String decodeValue(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
