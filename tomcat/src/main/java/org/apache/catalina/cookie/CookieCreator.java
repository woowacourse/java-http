package org.apache.catalina.cookie;

import java.util.HashMap;
import java.util.Map;

public class CookieCreator {

    private CookieCreator() {
    }

    public static Cookie create(String text) {
        Map<String, String> values = new HashMap<>();
        for (String pair : text.split("; ")) {
            String[] cookieComponents = pair.split("=");
            String key = cookieComponents[0];
            String value = cookieComponents[1];
            values.put(key, value);
        }
        return new Cookie(values);
    }
}
