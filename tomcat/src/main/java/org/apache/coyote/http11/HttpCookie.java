package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    
    private final Map<String, String> cookies = new HashMap<>();
    
    public HttpCookie(String cookieHeader) {
        parseCookies(cookieHeader);
    }
    
    private void parseCookies(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return;
        }
        
        String[] cookiePairs = cookieHeader.split(";");
        for (String cookiePair : cookiePairs) {
            String[] parts = cookiePair.trim().split("=", 2);
            if (parts.length == 2) {
                cookies.put(parts[0].trim(), parts[1].trim());
            }
        }
    }
    
    public String getValue(String name) {
        return cookies.get(name);
    }
    
    public String getJSessionId() {
        return getValue("JSESSIONID");
    }
    
    public static String createJSessionIdSetCookieHeader(String sessionId) {
        return "JSESSIONID=" + sessionId;
    }
}
