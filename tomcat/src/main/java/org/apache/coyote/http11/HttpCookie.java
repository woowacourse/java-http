package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public void addSessionId() {
        cookies.put("JSESSIONID", String.valueOf(UUID.randomUUID()));
    }

    public String getCookieValue(String name) {
        return cookies.get(name);
    }
}
