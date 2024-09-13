package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class HttpHeaders {

    private final Map<String, String> values = new HashMap<>();

    public void put(String key, String value) {
        values.put(key, value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public void forEach(BiConsumer<String, String> consumer) {
        values.forEach(consumer);
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "values=" + values +
                '}';
    }
}
