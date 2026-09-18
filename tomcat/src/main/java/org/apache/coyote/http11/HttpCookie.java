package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HttpCookie {

    public static final String JSESSION_ID = "JSESSIONID";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String rawCookie) {
        if (rawCookie == null || rawCookie.isBlank()) {
            return;
        }

        for (String cookie : rawCookie.split(";")) {
            String[] pair = cookie.trim().split("=", 2);

            if (pair.length == 2) {
                cookies.put(pair[0].trim(), pair[1].trim());
            }
        }
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(cookies.get(name));
    }

    public Optional<String> createJSessionIdIfAbsent() {
        if (cookies.containsKey(JSESSION_ID)) {
            return Optional.empty();
        }

        String sessionId = UUID.randomUUID().toString();
        cookies.put(JSESSION_ID, sessionId);
        return Optional.of(sessionId);
    }
}
