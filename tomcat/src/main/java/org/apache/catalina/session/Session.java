package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;

    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }

    public Object getAttribute(final String name) {
        return this.values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        this.values.put(name, value);
    }
}
