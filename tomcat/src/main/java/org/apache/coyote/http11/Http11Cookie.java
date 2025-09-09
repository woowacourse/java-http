package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Http11Cookie {

    private Map<String, String> cookies = new HashMap<>();

    public Http11Cookie(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            this.cookies = cookies;
            return;
        }

        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] parts = pair.trim().split("=", 2);
            if (parts.length == 2) {
                cookies.put(parts[0], parts[1]);
            }
        }

        this.cookies = cookies;
    }

    public boolean isContainsSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public boolean isNotContainsSessionId() {
        return !isContainsSessionId();
    }

    public String getSessionId() {
        if (isContainsSessionId()) {
            return cookies.get("JSESSIONID");
        } else  {
            return null;
        }
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
