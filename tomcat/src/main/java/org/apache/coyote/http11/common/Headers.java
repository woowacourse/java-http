package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> data = new HashMap<>();

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
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

    public int getContentLength() {
        String header = get("Content-Length");
        if (header == null) {
            return 0;
        }
        return Integer.parseInt(header);
    }
}
