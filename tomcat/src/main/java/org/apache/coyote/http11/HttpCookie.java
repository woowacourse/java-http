package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    
    private final Map<String, String> cookies;
    
    public HttpCookie() {
        this.cookies = new HashMap<>();
    }
    
    public HttpCookie(final String cookieHeader) {
        this.cookies = parseCookies(cookieHeader);
    }
    
    private Map<String, String> parseCookies(final String cookieHeader) {
        final Map<String, String> cookieMap = new HashMap<>();
        
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return cookieMap;
        }
        
        final String[] cookiePairs = cookieHeader.split(";");
        for (final String cookiePair : cookiePairs) {
            final String[] keyValue = cookiePair.trim().split("=", 2);
            if (keyValue.length == 2) {
                cookieMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        
        return cookieMap;
    }
    
    public String getValue(final String name) {
        return cookies.get(name);
    }
    
    public void addCookie(final String name, final String value) {
        cookies.put(name, value);
    }
    
    public boolean hasCookie(final String name) {
        return cookies.containsKey(name);
    }
    
    public Map<String, String> getAllCookies() {
        return new HashMap<>(cookies);
    }
}
