package org.apache.catalina.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final Map<String, String> cookies = new ConcurrentHashMap<>();
    private static final String AUTH_COOKIE_KEY = "JSESSIONID";

    public static String ofJSessionId(String id) {
        saveAuthCookie(id);
        return cookiesToString();
    }

    public static void setCookies(Map<String, String> cookies) {
        cookies.putAll(new HashMap<>(cookies));
    }

    public static String getId() {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return cookies.get(AUTH_COOKIE_KEY);
        }
        return UUID.randomUUID().toString();
    }

    public static void saveAuthCookie(String id) {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return;
        }
        HttpCookie.cookies.put(AUTH_COOKIE_KEY, id);
    }

    public static String cookiesToString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    private HttpCookie() {}

}
