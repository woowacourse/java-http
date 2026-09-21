package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie create(String cookieHeader) {
        if(cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(new HashMap<>());
        }
        String[] cookieArray = cookieHeader.split(";");
        Map<String, String> cookies = new HashMap<>();
        for(String cookie : cookieArray) {
            cookie = cookie.trim();
            cookies.put(cookie.split("=")[0], cookie.split("=")[1]);
        }
        return new HttpCookie(cookies);
    }

    public boolean isSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public void setSessionId() {
        cookies.put("JSESSIONID", UUID.randomUUID().toString());
    }

    public String getSessionIdCookieName() {
        return SESSION_ID_COOKIE_NAME;
    }

    public String getSessionId() {
        return cookies.get(SESSION_ID_COOKIE_NAME);
    }
}
