package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Session {

    private final String id;
    private final ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.getOrDefault(name, null);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }
}
