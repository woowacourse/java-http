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

    public boolean isExistCookie() {
        return !cookies.isEmpty();
    }

    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }
}
