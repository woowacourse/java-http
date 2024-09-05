package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private static final Map<String, Object> values = new HashMap<>();
    private final String id;

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }
}
