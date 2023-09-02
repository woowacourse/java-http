package org.apache.coyote.http11.message;

import java.util.Map;

public class Cookie {

    private final Map<String, String> mappings;

    public Cookie(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public boolean hasKey(String key) {
        return mappings.containsKey(key);
    }

    public String getValue(String key) {
        return mappings.get(key);
    }
}
