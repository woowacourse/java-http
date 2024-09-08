package org.apache.catalina.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String AUTH_COOKIE_KEY = "JSESSIONID";
    private final Map<String, String> cookies = new ConcurrentHashMap<>();

    public HttpCookie(Map<String, String> cookies) {
        cookies.putAll(new HashMap<>(cookies));
    }

    public String ofJSessionId(String id) {
        saveAuthCookie(id);
        return cookiesToString();
    }

    public String getId() {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return cookies.get(AUTH_COOKIE_KEY);
        }
        return UUID.randomUUID().toString();
    }

    private void saveAuthCookie(String id) {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return;
        }
        cookies.put(AUTH_COOKIE_KEY, id);
    }

    private String cookiesToString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
