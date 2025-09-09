package org.apache.coyote.http11.constant;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        for (var cookie : cookieHeader.split("; ")) {
            final String[] parsedCookie = cookie.split("=");
            cookies.put(parsedCookie[0], parsedCookie[1]);
        }
    }

    public boolean containsJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSessionId() {
        if (!cookies.containsKey("JSESSIONID")) {
            throw new IllegalStateException("JSESSIONID가 존재하지 않습니다.");
        }
        return cookies.get("JSESSIONID");
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public String getCookieString() {
        return null;
    }
}
