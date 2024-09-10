package com.techcourse.http;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public String serialize() {
        StringBuilder cookieString = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookieString.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("; ");
        }
        return cookieString.toString();
    }

    public boolean isExist() {
        return !cookies.isEmpty();
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void clear() {
        cookies.clear();
    }
}
