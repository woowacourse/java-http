package org.apache.catalina.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final Map<String, String> cookies = new ConcurrentHashMap<>();
    private static final String AUTH_COOKIE_KEY = "JSESSIONID";

    public static String getCookie() {
        return cookiesToString(cookies);
    }

    public static void saveAuthCookie() {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return;
        }
        String cookie = UUID.randomUUID().toString();
        HttpCookie.cookies.put(AUTH_COOKIE_KEY, cookie);
    }

    public static String cookiesToString(Map<String, String> cookies) {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    private HttpCookie() {}
}
