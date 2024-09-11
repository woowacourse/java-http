package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> values = new HashMap<>();
    private final String id;

    public Session(String id) {
        this.id = id;
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
