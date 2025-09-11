package org.apache.coyote.http11.constant;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        for (var cookie : cookieHeader.split("; ")) {
            final String[] parsedCookie = cookie.split("=");
            cookies.put(parsedCookie[0], parsedCookie[1]);
        }
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
