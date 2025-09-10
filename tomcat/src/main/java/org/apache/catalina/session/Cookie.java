package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private Map<String, String> keyValues = new HashMap<>();

    public Cookie(String cookieHeader) {
        this.keyValues = parse(cookieHeader);
    }

    private Map<String, String> parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookies;
        }

        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.isEmpty()) {
                continue;
            }

            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue.length > 1
                    ? keyValue[1].trim()
                    : "";

            cookies.put(key, value);
        }

        return cookies;
    }

    public String getAttribute(String key) {
        return keyValues.get(key);
    }
}
