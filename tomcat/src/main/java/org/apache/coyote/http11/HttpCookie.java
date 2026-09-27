package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }
        for (final var cookie : cookieHeader.split(";")) {
            final var keyValue = cookie.trim().split("=", 2);
            if (keyValue.length == 2) {
                values.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    public String getValue(final String name) {
        return values.get(name);
    }

    public static String ofJSessionId(final String id) {
        return JSESSIONID + "=" + id + "; Path=/; HttpOnly";
    }
}
