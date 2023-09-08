package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String key) {
        if (values.containsKey(key)) {
            return values.get(key);
        }
        throw new IllegalArgumentException("세션이 존재하지 않습니다.");
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }

    public void removeAttribute(String key) {
        values.remove(key);
    }

    public void invalidate() {
        values.clear();
    }
}
