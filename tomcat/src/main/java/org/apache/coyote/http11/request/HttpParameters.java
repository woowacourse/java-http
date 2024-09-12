package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpParameters {

    private final Map<String, String> values = new HashMap<>();

    public void put(String key, String value) {
        values.put(key, value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }
}
