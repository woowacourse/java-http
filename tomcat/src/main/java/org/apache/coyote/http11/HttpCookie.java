package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(String cookieHeader) {
        Map<String, String> parsed = new HashMap<>();

        if (cookieHeader != null && !cookieHeader.isBlank()) {
            for (String cookie : cookieHeader.split(";")) {
                String[] nameAndValue = cookie.trim().split("=", 2);

                if (nameAndValue.length == 2 && !nameAndValue[0].isBlank()) {
                    parsed.put(nameAndValue[0].trim(), nameAndValue[1].trim());
                }
            }
        }

        this.values = Map.copyOf(parsed);
    }

    public String get(String name) {
        return values.get(name);
    }

    public void put(String name, String value) {
        values.put(name, value);
    }
}
