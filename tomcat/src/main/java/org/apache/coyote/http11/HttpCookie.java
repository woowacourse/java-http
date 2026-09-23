package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final HttpCookie EMPTY = new HttpCookie(Map.of());

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = Collections.unmodifiableMap(cookies);
    }

    public static HttpCookie from(String rawCookie) {
        Map<String, String> parsed = new HashMap<>();

        for (String pair : rawCookie.split(";")) {
            String trimmed = pair.strip();
            if (trimmed.isBlank()) {
                continue;
            }
            String name = trimmed.split("=")[0];
            String value = trimmed.split("=")[1];

            parsed.put(name, value);
        }
        return new HttpCookie(parsed);
    }

    public static HttpCookie empty() {
        return EMPTY;
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }
}
