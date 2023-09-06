package org.apache.cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(String cookieValue) {
        if (cookieValue == null || cookieValue.isBlank()) {
            return new Cookie(new HashMap<>());
        }
        Map<String, String> cookies = Arrays.stream(cookieValue.split("; "))
                .map(value -> value.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));
        return new Cookie(cookies);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public boolean hasJSessionId() {
        return this.cookies.containsKey("JSESSIONID");
    }
}
