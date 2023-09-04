package org.apache.coyote.http11;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
