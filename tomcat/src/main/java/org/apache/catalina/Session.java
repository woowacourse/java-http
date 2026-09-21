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
        return this.id;
    }

    public Object getAttribute(String name) {
        return this.values.get(name);
    }

    public void setAttribute(String name, Object value) {
        this.values.put(name, value);
    }

    public void removeAttribute(String name) {
        this.values.remove(name);
    }

    public void invalidate() {

    }
}
