package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    
    private final Map<String, String> cookies;
    
    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }
    
    public static HttpCookie from(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return new HttpCookie(cookies);
        }
        
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return new HttpCookie(cookies);
    }
    
    public String getValue(String name) {
        return cookies.get(name);
    }
    
    public boolean hasValue(String name) {
        return getValue(name) != null;
    }
}