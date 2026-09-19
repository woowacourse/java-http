package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }
}
