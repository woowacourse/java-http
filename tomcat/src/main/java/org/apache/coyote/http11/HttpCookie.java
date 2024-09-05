package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie() {
        this.cookie = new LinkedHashMap<>();
    }

    public void setCookie(String value) {
        for (String cookiePair : value.split("; ")) {
            String cookieName = cookiePair.split("=")[0];
            String cookieValue = cookiePair.split("=")[1];
            cookie.put(cookieName, cookieValue);
        }
    }

    public boolean isExists(String key) {
        return cookie.containsKey(key);
    }

    public String getValue(String key) {
        return cookie.get(key);
    }
}
