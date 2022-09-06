package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(String cookies) {
        return new HttpCookie(parseCookies(cookies));
    }

    public static Map<String, String> parseCookies(String input) {
        Map<String, String> parsedCookies = new HashMap<>();

        String[] cookies = input.split("; ");
        for (String cookie : cookies) {
            String[] parsedCookie = cookie.split("=");
            parsedCookies.put(parsedCookie[0], parsedCookie[1]);
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
