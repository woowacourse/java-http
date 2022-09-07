package org.apache.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import session.Cookie;

public class CookieParser {

    public static final String COOKIE_DELIMITER = "; ";
    public static final String KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    public static Map<String, String> parseCookies(final String cookieHeaderValue) {
        return Arrays.stream(cookieHeaderValue.split(COOKIE_DELIMITER))
                .map(i -> i.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(i -> i[KEY_INDEX], i -> i[VALUE_INDEX]));
    }

    public static boolean checkJSessionIdIsExist(final String cookieHeaderValue) {
        return parseCookies(cookieHeaderValue).containsKey("JSESSIONID");
    }

    public static String joinAllCookiesToString(final Map<String, Cookie> cookies) {
        return cookies.keySet().stream()
                .map(i -> toStringForHeader(cookies.get(i)))
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }

    public static String toStringForHeader(final Cookie cookie) {
        return cookie.getKey() + KEY_VALUE_DELIMITER + cookie.getValue();
    }
}
