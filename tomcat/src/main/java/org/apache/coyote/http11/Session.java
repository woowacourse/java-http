package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;
    private boolean created;

    private Session(final String id, final boolean created) {
        this.id = id;
        this.created = created;
        this.values = new LinkedHashMap<>();
    }

    public static Session init(final String id) {
        return new Session(id, true);
    }

    public void found() {
        this.created = false;
    }

    public boolean isCreated() {
        return created;
    }

    public String id() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.getOrDefault(name, null);
    }

    public void addAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public boolean hasAttribute(final String name) {
        return values.containsKey(name);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }
}
