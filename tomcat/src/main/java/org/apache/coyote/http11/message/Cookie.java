package org.apache.coyote.http11.message;

import java.util.Map;

public class Cookie {

    private final Map<String, String> cookie;

    public Cookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public boolean hasKey(String key) {
        return cookie.containsKey(key);
    }

    public String getKey(String key) {
        return cookie.get(key);
    }
}
