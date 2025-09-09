package org.apache.coyote.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            parseCookies(cookieHeader);
        }
    }

    public boolean hasJSESSIONID() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID);
    }

    public String generateJSESSIONID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void parseCookies(String cookieHeader) {
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                cookies.put(key, value);
            }
        }
    }
}

