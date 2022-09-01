package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean isContains(String key) {
        return this.cookies.containsKey(key);
    }

    public String get(String key) {
        return this.cookies.get(key);
    }
}
