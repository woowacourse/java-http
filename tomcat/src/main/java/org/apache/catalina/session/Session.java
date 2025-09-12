package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values;
    private volatile boolean isNew;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>();
        this.isNew = true;
    }

    public String getId() {
        return this.id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public boolean isNew() {
        return isNew;
    }

    public void activate() {
        this.isNew = false;
    }
}
