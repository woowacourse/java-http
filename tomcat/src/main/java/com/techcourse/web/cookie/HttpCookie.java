package com.techcourse.web.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private final Map<String, String> cookieMap;

    public HttpCookie() {
        this.cookieMap = new HashMap<>();
    }

    public static String getJsessionId(String cookie) {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.parseCookie(cookie);
        return httpCookie.getCookieValue("JSESSIONID");
    }

    public void parseCookie(String cookie) {
        if (cookie == null || cookie.isBlank()) {
            return;
        }
        for (String c : cookie.split(";")) {
            cookieMap.put(c.trim().split("=")[0], c.trim().split("=")[1]);
        }
    }

    public String getCookieValue(String key) {
        return cookieMap.get(key);
    }

    public String generateCookie() {
        String value = UUID.randomUUID().toString();
        cookieMap.put("JSESSIONID", value);
        return value;
    }
}
