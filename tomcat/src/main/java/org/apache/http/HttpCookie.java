package org.apache.http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie of(String cookie) {
        return new HttpCookie(parseCookie(cookie));
    }

    private static Map<String, String> parseCookie(String cookie) {
        String[] cookiePairs = cookie.split(COOKIE_SEPARATOR);

        Map<String, String> cookies = new HashMap<>();
        for (String pair : cookiePairs) {
            String[] keyAndValue = pair.split(KEY_VALUE_SEPARATOR);
            cookies.put(keyAndValue[0], keyAndValue[1]);
        }

        return cookies;
    }

    public String getValue(String key) {
        return cookie.get(key);
    }

    @Override
    public String toString() {
        return cookie.entrySet()
                .stream()
                .map(entry -> entry.getKey() + KEY_VALUE_SEPARATOR + entry.getValue())
                .collect(Collectors.joining(COOKIE_SEPARATOR));
    }
}
