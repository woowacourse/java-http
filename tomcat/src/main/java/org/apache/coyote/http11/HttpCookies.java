package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private static final String EMPTY_VALUE = "";
    
    private final Map<String, String> values = new HashMap<>();

    public void add(final String key, final String value) {
        values.put(key, value);
    }

    public String get(final String key) {
        return values.getOrDefault(key, EMPTY_VALUE);
    }
}
