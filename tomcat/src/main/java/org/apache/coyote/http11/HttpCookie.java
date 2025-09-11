package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    private HttpCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public static HttpCookie parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader != null) {
            String[] cookiePairs = cookieHeader.split(";");
            for (String cookie : cookiePairs) {
                String[] keyValue = cookie.trim().split("=", 2);
                if (keyValue.length == 2) {
                    cookies.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return new HttpCookie(cookies);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public Map<String, String> asMap() {
        return cookies;
    }
}
