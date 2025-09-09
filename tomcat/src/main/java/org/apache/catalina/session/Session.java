package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private boolean valid; // 세션이 살아있는지 여부

    public Session(final String id) {
        this.id = id;
        this.valid = true;
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

    public void invalidate() {
        values.clear();
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }
}
