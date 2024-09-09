package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public void setValue(String key, String value) {
        cookies.put(key, value);
    }

    public String getValue(String key) {
        return cookies.get(key);
    }

    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }
}
