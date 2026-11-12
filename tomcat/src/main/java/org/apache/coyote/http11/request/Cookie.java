package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Cookie {
    private final Map<String, String> values;

    private Cookie(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(new HashMap<>(values));
    }

    public static Cookie from(String header) {
        Map<String, String> values = new HashMap<>();
        if (header == null || header.isBlank()) {
            return new Cookie(values);
        }
        for (String pair : header.split(";")) {
            addCookie(values, pair);
        }
        return new Cookie(values);
    }

    private static void addCookie(Map<String, String> values, String pair) {
        String[] keyValue = pair.trim().split("=", 2);
        if (isValidCookie(keyValue)) {
            values.put(keyValue[0], keyValue[1]);
        }
    }

    private static boolean isValidCookie(String[] keyValue) {
        return keyValue.length == 2 && !keyValue[0].isBlank();
    }

    public String get(String name) {
        return values.get(name);
    }

    public Map<String, String> values() {
        return values;
    }
}
