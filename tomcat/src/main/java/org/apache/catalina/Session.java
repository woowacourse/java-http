package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private static final String JSESSIONID_PREFIX = "JSESSIONID=";
    private final String id;
    private final Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String parseJSessionId() {
        return JSESSIONID_PREFIX + id;
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

    public String getId() {
        return id;
    }
}
