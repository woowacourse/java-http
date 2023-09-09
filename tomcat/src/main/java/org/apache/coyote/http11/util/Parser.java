package org.apache.coyote.http11.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {
    private static final String COOKIES_DELIMITER = "; ";
    private static final String PARAM_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = PARAM_DELIMITER;
    private static final String PARAMS_DELIMITER = "&";

    private Parser() {
    }

    public static Map<String, String> queryParamParse(final String param) {
        return Arrays.stream(param.split(PARAMS_DELIMITER)).takeWhile(it -> !it.isEmpty())
                .map(it -> it.split(PARAM_DELIMITER)).collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public static Map<String, String> cookieParse(final String cookie) {
        return Arrays.stream(cookie.split(COOKIES_DELIMITER))
                .takeWhile(it -> !it.isEmpty())
                .map(it -> it.split(COOKIE_DELIMITER))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }
}
