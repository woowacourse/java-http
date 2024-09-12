package org.was.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
    }

    public String getId() {
        return id;
    }

    public boolean hasAttribute(String name) {
        return values.containsKey(name);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
