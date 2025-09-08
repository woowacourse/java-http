package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>();
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
}
