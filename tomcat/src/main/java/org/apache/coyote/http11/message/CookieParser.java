package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieParser {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_KEY_AND_VALUE_SEPARATOR = "=";
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    public Map<String, String> parseCookie(final String cookieValue) {
        if (cookieValue == null) {
            return Collections.EMPTY_MAP;
        }
        final String[] cookies = cookieValue.split(COOKIE_SEPARATOR);

        return Arrays.stream(cookies)
                .map(cookie -> cookie.split(COOKIE_KEY_AND_VALUE_SEPARATOR))
                .collect(Collectors.toMap(cookie -> cookie[COOKIE_KEY_INDEX],
                        cookie -> cookie[COOKIE_VALUE_INDEX]));
    }
}
