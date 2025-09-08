package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String getId() {
        return null;
    }

    public Object getAttribute(final String name) {
        return null;
    }

    public void setAttribute(final String name, final Object value) {
    }

    public void removeAttribute(final String name) {
    }

    public void invalidate() {
    }
}
