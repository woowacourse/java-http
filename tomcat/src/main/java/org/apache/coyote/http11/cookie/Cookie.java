package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
    public static final String KEY_VALUE_DELIMITER = "=";
    private static final String DELIMITER = ";";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> cookies = new HashMap<>();

    private Cookie() {
    }

    public static Cookie from(String cookieValues) {
        final Map<String, String> parsedCookie = Arrays.stream(cookieValues.split(DELIMITER))
                .map(cookieValue -> cookieValue.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        splitCookie -> splitCookie[KEY_INDEX],
                        splitCookie -> splitCookie[VALUE_INDEX]
                ));
        final Cookie cookie = new Cookie();
        cookie.addAll(parsedCookie);
        return cookie;
    }

    private void addAll(final Map<String, String> parsedCookie) {
        cookies.putAll(parsedCookie);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
