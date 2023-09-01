package org.apache.coyote.util;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public class HttpRequestUtil {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private HttpRequestUtil() {
    }

    public static Map<String, String> parseQueryString(final String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(query -> query.split("="))
                .collect(toMap(query -> query[KEY_INDEX], query -> query[VALUE_INDEX]));
    }
}
