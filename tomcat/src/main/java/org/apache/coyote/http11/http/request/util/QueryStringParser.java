package org.apache.coyote.http11.http.request.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private QueryStringParser() {}

    public static Map<String, String> parse(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return Map.of();
        }
        return Arrays.stream(queryString.split("&"))
                .map(s -> s.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> decode(arr[0]),
                        arr -> decode(arr[1]),
                        (oldVal, newVal) -> newVal
                ));
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }
}
