package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, String> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public void setAttribute(final String name, final String value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }
}
