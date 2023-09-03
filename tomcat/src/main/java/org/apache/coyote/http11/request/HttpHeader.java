package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> value = new HashMap<>();

    public void put(final String key, final String value) {
        this.value.put(key, value);
    }

    public boolean containsKey(final String key) {
        return this.value.containsKey(key);
    }

    public String get(final String key) {
        return this.value.get(key);
    }
}
