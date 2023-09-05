package org.apache.coyote.http11.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String KEY_PAIR_BRIDGE = "=";
    private static final String KEY_PAIR_PATTERN = "%s" + KEY_PAIR_BRIDGE + "%s";

    private QueryStringParser() {
    }

    public static Map<String, List<String>> parse(String query) {
        HashMap<String, List<String>> queryParams = new HashMap<>();
        String[] pairs = query.split(QUERY_PARAMETER_DELIMITER);

        for (String pair : pairs) {
            int equalsIndex = pair.indexOf(KEY_PAIR_BRIDGE);
            String key = extractKey(pair, equalsIndex);
            queryParams.computeIfAbsent(key, k -> new LinkedList<>())
                    .add(extractValue(pair, equalsIndex));
        }
        return queryParams;
    }

    private static String extractKey(String pair, int index) {
        if (index > 0) {
            return URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
        }
        return pair;
    }

    private static String extractValue(String pair, int idx) {
        if (idx > 0 && pair.length() > idx + 1) {
            return URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String parse(Map<String, List<String>> queryParams) {
        return queryParams.entrySet()
                .stream()
                .map(entry -> QueryStringParser.create(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(QUERY_PARAMETER_DELIMITER));
    }

    private static String create(String key, List<String> values) {
        return values.stream()
                .map(value -> String.format(KEY_PAIR_PATTERN, key, value))
                .collect(Collectors.joining(QUERY_PARAMETER_DELIMITER));
    }

}
