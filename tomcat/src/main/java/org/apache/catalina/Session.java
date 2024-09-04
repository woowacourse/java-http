package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;

    private final Map<String, Object> values;

    public Session(String id) {
        this.id = id;
        values = new HashMap<>();
    }

    public boolean hasAttribute(String id) {
        return values.containsKey(id);
    }

    public String getId() {
        return id;
    }

    public <T> void setAttribute(String key, T value) {
        values.put(key, value);
    }

    public <T> T getAttribute(String key) {
        if (values.containsKey(key)) {
            return (T) values.get(key);
        }
        return null;
    }
}
