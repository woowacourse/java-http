package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> cookieValues;

    public Cookie(Map<String, String> cookieValues) {
        this.cookieValues = cookieValues;
    }

    public Cookie() {
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
