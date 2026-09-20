package org.apache.coyote.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = Map.copyOf(cookies);
    }

    public static HttpCookie from(String cookieHeader) {
        if (cookieHeader.startsWith("Cookie: ")) {
            throw new IllegalArgumentException("\"Cookie: \" 헤더 이름이 포함되어 있습니다. 헤더 값만 전달해주세요.");
        }

        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader.isEmpty()) {
            return new HttpCookie(cookies);
        }

        for (String keyValue : cookieHeader.split(";")) {
            String[] split = keyValue.strip().split("=", 2);
            cookies.put(split[0], split[1]);
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
