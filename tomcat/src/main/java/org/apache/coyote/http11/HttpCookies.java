package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private final Map<String, String> cookies = new HashMap<>();

    public String getCookieValue(String cookieName) {
        return cookies.get(cookieName);
    }

    public void putCookie(String name, String value) {
        cookies.put(name, value);
    }
}
