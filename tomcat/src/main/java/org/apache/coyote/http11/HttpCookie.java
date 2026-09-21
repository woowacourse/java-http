package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null) {
            return;
        }

        for (String cookie : cookieHeader.split(";")) {
            final String[] cookieParts = cookie.split("=", 2);

            if (cookieParts.length == 2) {
                cookies.put(cookieParts[0].trim(), cookieParts[1].trim());
            }
        }
    }

    public String get(final String name) {
        return cookies.get(name);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }
}
