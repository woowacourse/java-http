package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(String id) {
        this.id = id;
        values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
