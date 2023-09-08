package org.apache.coyote.http11.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

    public static Map<String, String> queryParamParse(final String param) {
        return Arrays.stream(param.split("&")).takeWhile(it -> !it.isEmpty())
                .map(it -> it.split("=")).collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public static Map<String, String> cookieParse(final String cookie) {
        return Arrays.stream(cookie.split(";= "))
                .takeWhile(it -> !it.isEmpty())
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }
}
