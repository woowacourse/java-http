package org.apache.tomcat.util.http.parser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXPECTED_PAIR_LENGTH = 2;

    private QueryStringParser() {
    }

    public static Map<String, String> parse(String queryString) {
        return Arrays.stream(queryString.split(PARAMETER_DELIMITER))
                .map(pair -> pair.split(KEY_VALUE_DELIMITER))
                .filter(keyValue -> keyValue.length == EXPECTED_PAIR_LENGTH)
                .collect(Collectors.toMap(keyValue -> decode(keyValue[KEY_INDEX]), keyValue -> decode(keyValue[VALUE_INDEX])));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
