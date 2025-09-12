package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {
    
    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookieHeader) {
        Map<String, String> cookiePairs = new HashMap<>();
        if (cookieHeader != null && !cookieHeader.trim().isEmpty()) {
            String[] pairs = cookieHeader.split(";");
            for (String pair : pairs) {
                String[] keyValue = pair.trim().split("=", 2);
                if (keyValue.length == 2) {
                    cookiePairs.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }
        return new HttpCookie(cookiePairs);
    }
    
    public String getValue(String name) {
        return cookies.get(name);
    }
    
    public String getJSessionId() {
        return getValue("JSESSIONID");
    }
    
    public static Optional<String> getSessionId(String cookieHeader) {
        if (cookieHeader == null) {
            return Optional.empty();
        }
        
        HttpCookie cookie = HttpCookie.from(cookieHeader);
        return Optional.ofNullable(cookie.getJSessionId());
    }

    public static String createJSessionIdSetCookieHeader(String sessionId) {
        return "JSESSIONID=" + sessionId;
    }
}
