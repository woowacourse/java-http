package org.apache.coyote.request.parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParser {

    private static final String QUERY_SEPARATOR = "&";
    private static final String PARAM_SEPARATOR = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private QueryParser() {
    }

    public static Map<String, String> parse(String query) {
        List<String> keys = List.of(query.split(QUERY_SEPARATOR));
        return keys.stream()
                .map(key -> key.split(PARAM_SEPARATOR))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[KEY_INDEX],
                        keyValue -> keyValue[VALUE_INDEX]
                ));
    }
}
