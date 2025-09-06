package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    public static final String COOKIE_PAIR_DELIMITER = ";";
    public static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies = new HashMap<>();
    
    public HttpCookie(String cookieHeader) {
        if (cookieHeader != null && !cookieHeader.isBlank()) {
            parseCookies(cookieHeader);
        }
    }
    
    private void parseCookies(String cookieHeader) {
        String[] cookiePairs = cookieHeader.split(COOKIE_PAIR_DELIMITER);
        for (String pair : cookiePairs) {
            String[] keyValue = pair.trim().split(KEY_VALUE_DELIMITER, 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].strip(), keyValue[1].strip());
            }
        }
    }
    
    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }
    
    public boolean hasJSESSIONID() {
        return hasCookie("JSESSIONID");
    }
}
