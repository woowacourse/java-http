package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> values;

    public Cookie(Map<String, String> values) {
        this.values = values;
    }

    public static Cookie empty() {
        return new Cookie(new HashMap<>());
    }

    public static Cookie parse(String cookieHeader) {
        Map<String, String> values = new HashMap<>();
        String[] cookies = cookieHeader.split(";");

        for (String cookie : cookies) {
            String[] nameAndValue = cookie.trim().split("=", 2);
            if (nameAndValue.length == 2) {
                values.put(nameAndValue[0], nameAndValue[1]);
            }
        }

        return new Cookie(values);
    }

    public String getValue(String name) {
        return values.get(name);
    }
}
