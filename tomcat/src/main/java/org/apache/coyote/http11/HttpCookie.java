package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        String[] cookieParameters = cookieHeader.split(";");

        for (String parameter : cookieParameters) {
            String[] nameAndValue = parameter.trim().split("=", 2);

            if (nameAndValue.length == 2) {
                values.put(nameAndValue[0], nameAndValue[1]);
            }
        }
    }

    public String get(String name) {
        return values.get(name);
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }
}
