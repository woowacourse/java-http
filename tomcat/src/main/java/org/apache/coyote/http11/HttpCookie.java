package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String headerCookie) {
        if (headerCookie == null || headerCookie.isBlank()) {
            return;
        }
        String[] cookies = headerCookie.split(";");
        for (String s : cookies) {
            String[] kv = s.split("=", 2);
            if (kv.length == 2) {
                this.cookies.put(kv[0].trim(), kv[1].trim());
            }
        }
    }

    public boolean hasJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String createJSessionId() {
        String sessionId = UUID.randomUUID().toString();
        cookies.put("JSESSIONID", sessionId);
        return sessionId;
    }
}
