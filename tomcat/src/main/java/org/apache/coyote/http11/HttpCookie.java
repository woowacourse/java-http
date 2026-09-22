package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String allCookies) {
        parseCookies(allCookies);
    }

    private void parseCookies(String allCookies) {
        if (allCookies.isBlank()) {
            return;
        }
        String[] cookies = allCookies.split(";");
        for (String cookie : cookies) {
            cookie = cookie.trim();
            String[] pair = cookie.split("=", 2);
            this.cookies.put(pair[0], pair[1]);
        }
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }
}
