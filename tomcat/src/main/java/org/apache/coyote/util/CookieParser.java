package org.apache.coyote.util;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.common.HttpCookie;

public class CookieParser {

    private static final String DELIMITER = "; ";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private CookieParser() {
    }

    public static HttpCookie parse(String cookieString) {
        if (cookieString == null || cookieString.isBlank()) {
            return new HttpCookie(Collections.emptyMap());
        }
        Map<String, String> cookieMap = Arrays.stream(cookieString.split(DELIMITER))
            .map(cookie -> cookie.split("=", 2))
            .collect(toMap(nameToValue -> nameToValue[NAME_INDEX], CookieParser::parseValue));
        return new HttpCookie(cookieMap);
    }

    private static String parseValue(String[] value) {
        if (value.length > 1) {
            return value[VALUE_INDEX];
        }
        return "";
    }
}
