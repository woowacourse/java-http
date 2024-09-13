package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CookieParser {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_KEY_AND_VALUE_SEPARATOR = "=";
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    public Map<String, String> parseCookie(final String cookieValue) {
        final HashMap<String, String> result = new HashMap<>();
        if (cookieValue == null) {
            return Collections.EMPTY_MAP;
        }
        final String[] cookies = cookieValue.split(COOKIE_SEPARATOR);

        Arrays.stream(cookies)
                .forEach(cookie -> {
                    final String[] keyAndValue = cookie.split(COOKIE_KEY_AND_VALUE_SEPARATOR);
                    result.put(keyAndValue[COOKIE_KEY_INDEX], keyAndValue[COOKIE_VALUE_INDEX]);
                });
        return result;
    }
}
