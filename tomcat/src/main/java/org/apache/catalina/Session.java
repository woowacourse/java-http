package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    private Session(final String id, final Map<String, Object> values) {
        this.id = id;
        this.values = values;
    }

    public Session(final String id) {
        this(id, new HashMap<>());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void addAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }
}
