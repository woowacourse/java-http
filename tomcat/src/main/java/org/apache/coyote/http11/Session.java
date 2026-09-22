package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        this.values = new LinkedHashMap<>();
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
