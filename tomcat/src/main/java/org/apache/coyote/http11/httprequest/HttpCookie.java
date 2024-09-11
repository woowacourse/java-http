package org.apache.coyote.http11.httprequest;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public boolean containsCookie(String key) {
        return cookies.containsKey(key);
    }

    public String getCookieValue(String key) {
        return cookies.get(key);
    }
}
