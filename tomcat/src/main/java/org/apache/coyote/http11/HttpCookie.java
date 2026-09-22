package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader == null) {
            return;
        }
        for (String cookie : cookieHeader.split(";")) {
            String[] keyValue = cookie.split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
