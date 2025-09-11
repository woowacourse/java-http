package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> cookies = new HashMap<>();

    private Cookie() {
    }

    public static Cookie fromHeader(String cookieHeader) {
        Cookie cookie = new Cookie();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookie;
        }

        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=", 2);
            if (keyValue.length == 2) {
                cookie.cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return cookie;
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public void add(String name, String value) {
        cookies.put(name, value);
    }
}
