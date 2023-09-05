package org.apache.coyote.http11.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StringFormatParser {

    private StringFormatParser() {
    }

    public static Map<String, List<String>> parseMultiValuePairs(
            final String value,
            final String delimiter,
            final String bridge
    ) {
        final HashMap<String, List<String>> queryParams = new HashMap<>();
        final var pairs = value.split(delimiter);

        for (final String pair : pairs) {
            final var equalsIndex = pair.indexOf(bridge);
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

}
