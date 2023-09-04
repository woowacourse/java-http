package org.apache.coyote.http.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private final Map<String, String> cookie;

    private Cookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static Cookie from(final String rawCookies) {
        final Map<String, String> cookies = new HashMap<>();

        for (String cookie : rawCookies.split(";")) {
            String[] keyValuePair = cookie.trim().split("=");
            cookies.put(keyValuePair[0], keyValuePair[1]);
        }

        return new Cookie(cookies);
    }

    public void put(final String key, final String value) {
        this.cookie.put(key, value);
    }

    public boolean hasCookie(final String key) {
        return cookie.containsKey(key);
    }

    public static Cookie emptyCookie() {
        return new Cookie(new HashMap<>());
    }

    public String getCookie(final String key) {
        return cookie.get(key);
    }

    public String toRawCookie() {
        return cookie.entrySet().stream()
                .map(it -> String.join("=", it.getKey(), it.getValue()))
                .collect(Collectors.joining("; "));
    }

    public int getSize(){
        return cookie.size();
    }
}
