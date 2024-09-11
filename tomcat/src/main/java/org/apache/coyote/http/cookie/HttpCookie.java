package org.apache.coyote.http.cookie;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = ";";
    private static final String KEY_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_PAIR = 2;

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String rawCookies) {
        if (rawCookies == null || !rawCookies.contains(JSESSIONID)) {
            throw new IllegalArgumentException("쿠키 값이 비어있습니다.");
        }

        return new HttpCookie(Arrays.stream(rawCookies.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.trim().split(KEY_DELIMITER, KEY_VALUE_PAIR))
                .collect(Collectors.toMap(
                        result -> result[KEY_INDEX],
                        result -> result[VALUE_INDEX]
                ))
        );
    }
}
