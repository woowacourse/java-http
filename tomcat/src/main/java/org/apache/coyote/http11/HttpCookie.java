package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie() {
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public boolean containsCookie(String key) {
        return cookies.containsKey(key);
    }
}
