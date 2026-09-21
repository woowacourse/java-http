package org.apache.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String jsessionId;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String jsessionId) {
        this.jsessionId = jsessionId;
    }

    public String getJsessionId() {
        return jsessionId;
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
