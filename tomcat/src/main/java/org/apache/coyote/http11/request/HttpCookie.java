package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getCookieValue(String key) {
        return this.cookies.getOrDefault(key, "");
    }
}
