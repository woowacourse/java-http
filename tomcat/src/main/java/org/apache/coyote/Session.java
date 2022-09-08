package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session init() {
        return new Session(UUID.randomUUID().toString());
    }

    public String getId() {
        return this.id;
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
