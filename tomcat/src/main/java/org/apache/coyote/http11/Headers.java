package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers = new HashMap<>();

    public void put(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var set : headers.entrySet()) {
            sb.append(set.getKey()).append(": ").append(set.getValue()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public void clear() {
        headers.clear();
    }
}
