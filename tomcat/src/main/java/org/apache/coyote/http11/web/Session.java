package org.apache.coyote.http11.web;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        values = new HashMap<>();
    }

    public void setAttribute(final String id, final Object value) {
        values.put(id, value);
    }

    public String getId() {
        return id;
    }
}
