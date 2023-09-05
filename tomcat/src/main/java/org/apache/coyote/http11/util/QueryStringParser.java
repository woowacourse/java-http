package org.apache.coyote.http11.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String KEY_PAIR_BRIDGE = "=";
    private static final String KEY_PAIR_PATTERN = "%s" + KEY_PAIR_BRIDGE + "%s";

    private QueryStringParser() {
    }

    public static Map<String, List<String>> parse(final String query) {
        final HashMap<String, List<String>> queryParams = new HashMap<>();
        final var pairs = query.split(QUERY_PARAMETER_DELIMITER);

        for (final String pair : pairs) {
            final var equalsIndex = pair.indexOf(KEY_PAIR_BRIDGE);
            final var key = extractKey(pair, equalsIndex);
            queryParams.computeIfAbsent(key, k -> new LinkedList<>())
                    .add(extractValue(pair, equalsIndex));
        }

        return queryParams;
    }

    private static String extractKey(final String pair, final int index) {
        if (index > 0) {
            return URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
        }

        return pair;
    }

    private static String extractValue(final String pair, final int index) {
        if (index > 0 && pair.length() > index + 1) {
            return URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8);
        }

        return null;
    }

    public static String parse(final Map<String, List<String>> queryParams) {

        return queryParams.entrySet()
                .stream()
                .map(QueryStringParser::create)
                .collect(Collectors.joining(QUERY_PARAMETER_DELIMITER));
    }

    private static String create(final Entry<String, List<String>> entry) {

        return entry.getValue()
                .stream()
                .map(value -> String.format(KEY_PAIR_PATTERN, entry.getKey(), value))
                .collect(Collectors.joining(QUERY_PARAMETER_DELIMITER));
    }

}
