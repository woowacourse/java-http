package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        for (String cookie : cookieHeader.split(";")) {
            String[] cookieParts = cookie.split("=", 2);

            if (cookieParts.length != 2) {
                continue;
            }

            String name = cookieParts[0].trim();
            String value = cookieParts[1].trim();
            cookies.putIfAbsent(name, value);
        }
    }

    public String getValue(String name) {
        return cookies.getOrDefault(name, "");
    }
}
