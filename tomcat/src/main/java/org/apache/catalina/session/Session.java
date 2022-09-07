package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }
}
