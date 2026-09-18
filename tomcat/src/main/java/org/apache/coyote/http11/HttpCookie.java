package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    HttpCookie(String cookieHeader) {
        cookies = parseCookies(cookieHeader);
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    private Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookieMap = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookieMap;
        }

        String[] tokens = cookieHeader.split(";");
        for (String token : tokens) {
            String[] keyValue = token.trim().split("=", 2);
            if (keyValue.length == 2) {
                cookieMap.put(keyValue[0], keyValue[1]);
            }
        }
        return cookieMap;
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
