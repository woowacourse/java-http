package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new LinkedHashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] nameAndValue = cookie.trim().split("=", 2);
            if (nameAndValue.length == 2) {
                cookies.put(nameAndValue[0], nameAndValue[1]);
            }
        }
    }

    public boolean contains(String name) {
        return cookies.containsKey(name);
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
