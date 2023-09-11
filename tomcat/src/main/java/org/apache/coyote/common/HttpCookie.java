package org.apache.coyote.common;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String KEY_VALUE_SEPERATOR = "=";
    private static final String COOKIE_SEPERATOR = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie of(String cookieString) {
        Map<String, String> cookies = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(cookieString)) {
            String[] cookiePairs = cookieString.split(COOKIE_SEPERATOR);
            generateCookie(cookies, cookiePairs);
        }

        return new HttpCookie(cookies);
    }

    private static void generateCookie(Map<String, String> cookies, String[] cookiePairs) {
        for (String cookiePair : cookiePairs) {
            String[] parts = cookiePair.split(KEY_VALUE_SEPERATOR);
            if (parts.length == 2) {
                String name = parts[KEY_INDEX];
                String value = parts[VALUE_INDEX];
                cookies.put(name, value);
            }
        }
    }

    public String getValue(String key) {
        return cookie.get(key);
    }

    public String convertToHeader() {
        return cookie.entrySet()
                .stream()
                .map(entry -> entry.getKey() + KEY_VALUE_SEPERATOR + entry.getValue())
                .collect(Collectors.joining(COOKIE_SEPERATOR));
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "cookie=" + cookie +
                '}';
    }
}
