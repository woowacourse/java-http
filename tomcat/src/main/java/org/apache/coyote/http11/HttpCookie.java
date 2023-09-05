package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private Map<String, String> cookies = new ConcurrentHashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }
}
