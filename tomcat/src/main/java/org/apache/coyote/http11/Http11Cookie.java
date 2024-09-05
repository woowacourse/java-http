package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Http11Cookie {

    private final Map<String, String> cookies = new HashMap<>();

    public Http11Cookie(String rawCookies) {
        if (rawCookies.isEmpty()) {
            return;
        }
        String[] cookies = rawCookies.split("; ");
        for (String cookie : cookies) {
            String key = cookie.split("=")[0];
            String value = cookie.split("=")[1];
            this.cookies.put(key, value);
        }
    }

    public boolean containsCookieKey(String key) {
        return cookies.containsKey(key);
    }

    public String getCookieValue(String key) {
        return cookies.get(key);
    }
}
