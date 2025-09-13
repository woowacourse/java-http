package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session implements org.apache.coyote.Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(final String name) {
        return values.get(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
