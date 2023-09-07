package org.apache.catalina.servlet.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    private boolean invalidate;

    public Session(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public Object getAttribute(String name) {
        validate();
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        validate();
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        validate();
        values.remove(name);
    }

    private void validate() {
        if (invalidate) {
            throw new InvalidateSessionException();
        }
    }

    public void invalidate() {
        values.clear();
        invalidate = true;
    }

    public boolean isInvalidate() {
        return invalidate;
    }
}
