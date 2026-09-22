package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        final String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            final String[] keyValue = cookie.trim().split("=", 2);
            if (keyValue.length < 2) {
                continue;
            }
            values.put(keyValue[0].trim(), keyValue[1].trim());
        }
    }

    public String getValue(final String name) {
        return values.get(name);
    }
}
