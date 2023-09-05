package org.apache.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Cookie {

    private final Map<String, String> cookies;

    public static Cookie parse(final String cookieValue) {
        final Map<String, String> cookies = new HashMap<>();
        Arrays.stream(cookieValue.split("; "))
                .forEach(cookie -> {
                    final String[] keyValue = cookie.split("=");
                    cookies.put(keyValue[0], keyValue[1]);
                });
        return new Cookie(cookies);
    }

    public static Cookie empty() {
        return new Cookie(new HashMap<>());
    }

    private Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void addCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public boolean containsKey(final String key) {
        return cookies.containsKey(key);
    }

    public String generateCookieHeaderValue() {
        final List<String> cookies = this.cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.toList());
        return String.join("; ", cookies);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }
}
