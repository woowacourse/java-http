package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {
    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(String header) {
        Map<String, String> values = new LinkedHashMap<>();

        if (header == null || header.isBlank()) {
            return new HttpCookie(values);
        }

        for (String token : header.split(";")) {
            String cookie = token.trim();
            int separator = cookie.indexOf('=');
            if (separator <= 0) {
                continue;
            }

            String name = cookie.substring(0, separator).trim();
            String value = cookie.substring(separator + 1).trim();
            values.put(name, value);
        }

        return new HttpCookie(values);
    }

    public static HttpCookie ofJSessionId(String sessionId) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put(JSESSIONID, sessionId);
        return new HttpCookie(values);
    }

    public String get(String name) {
        return values.get(name);
    }

    public String toHeaderValue() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + "; " + right)
                .orElse("");
    }
}
