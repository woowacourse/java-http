package org.apache.coyote.http11.domain;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    private Map<String, String> cookies;

    public Cookie() {
        cookies = new HashMap<>();
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
