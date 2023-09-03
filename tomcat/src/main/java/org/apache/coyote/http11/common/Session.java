package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final UUID id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(UUID id) {
        this.id = id;
    }

    public UUID getId() {
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

    public void invalidate() {
        values.clear();
    }
}
