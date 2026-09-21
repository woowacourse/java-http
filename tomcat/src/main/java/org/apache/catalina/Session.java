package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public Session(String id, String key, Object value) {
        this.id = id;
        values.put(key, value);
    }

    public String getId() {
        return id;
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public Object getAttribute(String key) {
        return values.get(key);
    }
}
