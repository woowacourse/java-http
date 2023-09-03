package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String COOKIE_DELIMITER = ";";

    private final Map<String, String> cookies;

    private Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(String cookieValue) {
        if (cookieValue.isBlank()) {
            return new Cookie(Map.of());
        }

        Map<String, String> cookies = Arrays.stream(cookieValue.split(COOKIE_DELIMITER))
                .map(cookieElement -> cookieElement.trim().split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));
        return new Cookie(cookies);
    }

    public String findByKey(String key) {
        return cookies.get(key);
    }
}
