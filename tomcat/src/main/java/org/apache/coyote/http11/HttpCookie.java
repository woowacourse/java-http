package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader != null && !cookieHeader.isBlank()) {
            String[] pairs = cookieHeader.split(";");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    cookies.put(keyValue[0], keyValue[1]);
                } else if (keyValue.length == 1) {
                    cookies.put(keyValue[0], "");
                }
            }
        }
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
