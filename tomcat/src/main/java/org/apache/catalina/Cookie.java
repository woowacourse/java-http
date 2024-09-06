package org.apache.catalina;

import java.util.Map;

public class Cookie {

    private final Map<String, String> values;

    public Cookie(Map<String, String> values) {
        this.values = values;
    }

    public String getValue(String key) {
        return values.getOrDefault(key, null);
    }
}
