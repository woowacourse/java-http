package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(cookies);
        }

        for (String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2) {
                cookies.put(pair[0], pair[1]);
            }
        }
        return new HttpCookie(cookies);
    }

    public Optional<String> get(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    public static String newSessionId() {
        return UUID.randomUUID().toString();
    }

    public static String newJSessionId() {
        return JSESSIONID + "=" + newSessionId();
    }
}
