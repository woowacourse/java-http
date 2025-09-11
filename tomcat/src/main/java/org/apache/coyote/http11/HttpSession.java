package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private boolean invalidated = false;

    public HttpSession(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        checkValid();
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        checkValid();
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        checkValid();
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
        invalidated = true;
    }

    private void checkValid() {
        if (invalidated) {
            throw new IllegalStateException("Session is invalidated");
        }
    }
}
