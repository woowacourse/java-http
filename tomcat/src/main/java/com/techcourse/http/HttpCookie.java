package com.techcourse.http;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpCookie {

    public static final String SEPARATOR = "; ";
    public static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;
    private boolean httpOnly;

    public HttpCookie() {
        this.cookies = new HashMap<>();
        this.httpOnly = false;
    }

    public String serialize() {
        StringBuilder cookieString = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookieString.append(entry.getKey())
                    .append(KEY_VALUE_SEPARATOR)
                    .append(entry.getValue())
                    .append(SEPARATOR);
        }
        if (httpOnly) {
            cookieString.append(" HttpOnly");
        }
        return cookieString.toString();
    }

    public boolean isExist() {
        return !cookies.isEmpty();
    }

    public void clear() {
        cookies.clear();
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
