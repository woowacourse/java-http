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

    public String getSessionId() {
        return cookies.get("JSESSIONID");
    }

    public boolean containsJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }
}
