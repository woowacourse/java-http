package org.apache.catalina.auth;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String AUTH_COOKIE_KEY = "JSESSIONID";
    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new ConcurrentHashMap<>(cookies);
    }

    public String getCookies(String id) {
        saveAuthCookie(id);
        return cookiesToString();
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

    public String getId() {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return cookies.get(AUTH_COOKIE_KEY);
        }
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(cookies, that.cookies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cookies);
    }
}
