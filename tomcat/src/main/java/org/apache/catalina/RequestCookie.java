package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class RequestCookie {

    private final Map<String, String> cookieValues;

    public RequestCookie(Map<String, String> cookieValues) {
        this.cookieValues = cookieValues;
    }

    public RequestCookie() {
        this.cookieValues = new HashMap<>();
    }

    public void add(String key, String value) {
        this.cookieValues.put(key, value);
    }

    public String findByKey(String key) {
        return this.cookieValues.get(key);
    }

    public Map<String, String> getCookieValues() {
        return cookieValues;
    }
}
