package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String allCookies) {
        parseCookies(allCookies);
    }

    private void parseCookies(String allCookies) {
        if (allCookies.isBlank()) {
            return;
        }
        String[] cookies = allCookies.split(";");
        for (String cookie : cookies) {
            cookie = cookie.trim();
            String[] pair = cookie.split("=", 2);
            this.cookies.put(pair[0], pair[1]);
        }
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String addJSessionId() {
        UUID uuid = UUID.randomUUID();
        cookies.put("JSESSIONID", uuid.toString());
        return cookies.get("JSESSIONID");
    }
}
