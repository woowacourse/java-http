package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> keyValues = new HashMap<>();

    public HttpCookie(final String body) {
        for (String keyValue : body.split("; ")) {
            int index = keyValue.indexOf("=");
            String key = keyValue.substring(0, index);
            String value = keyValue.substring(index + 1);
            keyValues.put(key, value);
        }
    }

    public String getValue(final String key) {
        return keyValues.get(key);
    }
}
