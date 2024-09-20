package org.apache.catalina.coockie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final int KEY_VALUE_COUNT = 2;
    private static final String JSESSIONID_VALUE = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpCookie(String rawCookies) {
        this.cookies = mapCookies(rawCookies);
    }

    private static Map<String, String> mapCookies(String rawCookies) {
        if (rawCookies == null || rawCookies.isBlank()) {
            return Map.of();
        }

        return Arrays.stream(rawCookies.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.split(COOKIE_VALUE_DELIMITER))
                .filter(cookiePair -> cookiePair.length == KEY_VALUE_COUNT)
                .collect(Collectors.toMap(cookiePair -> cookiePair[KEY_INDEX], cookiePair -> cookiePair[VALUE_INDEX]));
    }

    public boolean hasSessionId() {
        return cookies.containsKey(JSESSIONID_VALUE);
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID_VALUE);
    }

    public boolean hasCookie() {
        return !cookies.isEmpty();
    }

    public String getResponse() {
        if (cookies.isEmpty()) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        for (Entry<String, String> stringStringEntry : cookies.entrySet()) {
            response.append(stringStringEntry.getKey())
                    .append(COOKIE_VALUE_DELIMITER)
                    .append(stringStringEntry.getValue())
                    .append(";");
        }
        return String.valueOf(response);
    }
}
