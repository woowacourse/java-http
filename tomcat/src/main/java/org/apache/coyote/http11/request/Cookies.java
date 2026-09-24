package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookies {

    private final Map<String, Cookie> values;

    private Cookies(Map<String, Cookie> values) {
        this.values = values;
    }

    public static Cookies from(String cookieHeader) {
        Map<String, Cookie> values = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new Cookies(values);
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] nameAndValue = cookie.trim().split("=", 2);
            if (nameAndValue.length == 2) {
                values.put(nameAndValue[0], new Cookie(nameAndValue[0], nameAndValue[1]));
            }
        }

        return new Cookies(values);
    }

    public Optional<Cookie> find(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
