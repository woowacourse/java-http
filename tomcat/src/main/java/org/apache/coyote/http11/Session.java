package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
