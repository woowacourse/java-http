package org.apache.coyote.http11.message.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_PAIR_DELIMITER = "=";
    private static final int HEADER_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookieData;

    private Cookie(final Map<String, String> cookieData) {
        this.cookieData = cookieData;
    }

    public static Cookie from(final String value) {
        if (value == null) {
            return new Cookie(Collections.emptyMap());
        }

        final String[] cookies = value.split(COOKIE_DELIMITER);
        final Map<String, String> cookieData = Arrays.stream(cookies)
                .map(cookie -> cookie.split(COOKIE_PAIR_DELIMITER))
                .collect(Collectors.toMap(cookiePair -> cookiePair[HEADER_INDEX],
                        cookiePair -> cookiePair[VALUE_INDEX]));

        return new Cookie(cookieData);
    }

    public String get(final String key) {
        return cookieData.get(key);
    }
}
