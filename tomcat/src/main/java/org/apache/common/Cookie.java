package org.apache.common;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> cookies;

    private Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(String cookies) {
        if (cookies == null || cookies.isBlank()) {
            return new Cookie(new HashMap<>());
        }

        return Arrays.stream(cookies.split(COOKIE_SEPARATOR))
                .map(cookie -> cookie.split(COOKIE_DELIMITER))
                .collect(collectingAndThen(
                        toMap(cookie -> cookie[0], cookie -> cookie[1]),
                        Cookie::new)
                );
    }

    public String getValue(String key) {
        return cookies.getOrDefault(key, null);
    }
}
