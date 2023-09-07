package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private String id;
    private Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Optional<Object> getAttribute(final String name) {
        return Optional.ofNullable(values.get(name));
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
