package org.apache.coyote.http11;

import java.util.LinkedHashMap;

public class Headers {

    private final LinkedHashMap<String, String> values;

    public Headers(final LinkedHashMap<String, String> values) {
        this.values = values;
    }

    public void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public boolean hasHeader(final String key) {
        return values.containsKey(key);
    }

    public String getHeader(final String key) {
        return values.get(key);
    }

    public LinkedHashMap<String, String> getValues() {
        return new LinkedHashMap<>(values);
    }
}
