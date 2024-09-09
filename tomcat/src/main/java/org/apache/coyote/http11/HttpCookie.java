package org.apache.coyote.http11;

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

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String getValue(String key) {
        return cookies.get(key);
    }
}
