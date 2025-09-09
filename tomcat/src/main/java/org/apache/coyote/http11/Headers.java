package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> data = new HashMap<>();

    public void put(String key, String value) {
        data.put(key, value);
    }

    public void clear() {
        data.clear();
    }

    @Override
    public String toString() {
        if (data.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (var set : data.entrySet()) {
            sb.append(set.getKey()).append(": ").append(set.getValue());
        }
        return sb.toString();
    }
}
