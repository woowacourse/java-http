package org.apache.coyote.http11.parser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieParser {

    private static final String COOKIE_DELIMITER = ";";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private CookieParser() {
    }

    public static Map<String, String> parse(final String cookieValue) {
        final String[] params = cookieValue.split(COOKIE_DELIMITER);

        return Arrays.stream(params).map(param -> param.split(COOKIE_VALUE_DELIMITER))
            .filter(keyValue -> keyValue.length == 2)
            .collect(Collectors.toMap(
                keyValue -> keyValue[KEY_INDEX],
                keyValue -> keyValue[VALUE_INDEX],
                (prev, update) -> update
            ));
    }
}
