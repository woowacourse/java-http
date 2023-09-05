package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> params = new HashMap<>();

    public Cookie(String cookie) {
        String[] paramsSplit = cookie.split("; ");
        for (String param : paramsSplit) {
            String[] keyValue = param.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
    }

    public String getValue(String key) {
        return params.get(key);
    }
}
