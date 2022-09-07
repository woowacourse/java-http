package org.apache.coyote.http11.web;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        values = new HashMap<>();
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String id, final Object value) {
        values.put(id, value);
    }

    public void removeAttribute(final String id) {
        values.remove(id);
    }

    public String getId() {
        return id;
    }
}
