package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    public String getSession(String cookie) {
        Map<String, String> cookies = parseCookie(cookie);
        return cookies.get("JSESSIONID");
    }

    public String getCookieSession() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private Map<String, String> parseCookie(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] cookie is null");
        }

        String header = cookieHeader;
        if (header.startsWith("Cookie: ")) {
            header = header.substring(8);
        }

        String[] keyValues = header.split(";");
        for (String keyValue : keyValues) {
            String[] pair = keyValue.trim().split("=", 2);
            if (pair.length == 2) {
                cookies.put(pair[0].trim(), pair[1].trim());
            }
        }

        return cookies;
    }
}
