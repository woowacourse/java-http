package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = parse(cookieHeader);
    }

    private Map<String, String> parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }
        for (String pair : cookieHeader.split(";")) {
            String[] keyValue = pair.trim().split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return cookies;
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
