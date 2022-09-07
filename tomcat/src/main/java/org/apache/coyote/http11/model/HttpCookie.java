package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(String cookies) {
        return new HttpCookie(parseCookies(cookies));
    }

    public static Map<String, String> parseCookies(String input) {
        Map<String, String> parsedCookies = new HashMap<>();

        String[] cookies = input.split(COOKIE_SEPARATOR);
        for (String cookie : cookies) {
            String[] parsedCookie = cookie.split(KEY_VALUE_SEPARATOR);
            parsedCookies.put(parsedCookie[KEY], parsedCookie[VALUE]);
        }
        return parsedCookies;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public String getValue(String key) {
        return values.get(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
