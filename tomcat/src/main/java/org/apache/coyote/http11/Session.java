package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {

    }

    public void setAttribute(final String name, final Object value) {

    }

    public void removeAttribute(final String name) {

    }

    public void invalidate() {

    }
}
