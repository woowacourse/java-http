package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> values;

    public Cookie(Map<String, String> values) {
        this.values = values;
    }

    public static Cookie from(String cookies) {
        if (cookies == null) {
            return new Cookie(Collections.emptyMap());
        }

        Map<String, String> parseResultOfCookies = new HashMap<>();

        for (String cookie : cookies.split(";")) {
            putParseResultOfComponent(parseResultOfCookies, cookie);
        }

        return new Cookie(parseResultOfCookies);
    }

    public static Cookie empty() {
        return new Cookie(new HashMap<>());
    }
    private static void putParseResultOfComponent(
            Map<String, String> parseResultOfRequestBody,
            String component
    ) {
        if (component.length() < 2) {
            return;
        }

        String[] keyAndValue = component.split("=");
        parseResultOfRequestBody.put(keyAndValue[0].trim(), keyAndValue[1].trim());
    }

    public String get(String key) {
        return values.getOrDefault(key, "");
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public Map<String, String> getValues() {
        return values;
    }

}
