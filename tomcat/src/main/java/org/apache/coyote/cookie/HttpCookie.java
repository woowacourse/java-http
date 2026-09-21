package org.apache.coyote.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = Map.copyOf(cookies);
    }

    public static HttpCookie from(String cookieString) {
        if (cookieString.isEmpty()) {
            return new HttpCookie(Map.of());
        }
        if (cookieString.startsWith("Cookie: ")) {
            throw new IllegalArgumentException("잘못된 Cookie 형식입니다: " + cookieString);
        }

        Map<String, String> cookies = new HashMap<>();
        for (String keyValue : cookieString.split(";")) {
            String cookie = keyValue.strip();
            int separatorIndex = cookie.indexOf('=');
            if (separatorIndex <= 0) {
                throw new IllegalArgumentException("잘못된 Cookie 형식입니다: " + cookieString);
            }
            String name = cookie.substring(0, separatorIndex).strip();
            String value = cookie.substring(separatorIndex + 1).strip();
            cookies.put(name, value);
        }
        return new HttpCookie(cookies);
    }

    public boolean has(String name) {
        return cookies.containsKey(name);
    }

    public Optional<String> getValue(String name) {
        return Optional.ofNullable(cookies.get(name));
    }

    public Map<String, String> cookies() {
        return Map.copyOf(cookies);
    }
}
