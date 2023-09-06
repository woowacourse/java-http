package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public static HttpCookie from(String cookie) {
        final Map<String, String> cookies = new HashMap<>();

        String[] keyValue = cookie.split("=");
        cookies.put(keyValue[0].trim(), keyValue[1]);

        return new HttpCookie(cookies);
    }

    public String getByKey(String key) {
        return cookies.getOrDefault(key, null);
    }
}
