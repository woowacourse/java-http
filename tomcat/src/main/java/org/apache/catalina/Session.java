package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> sessions = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public boolean hasAttribute(final String name) {
        return sessions.containsKey(name);
    }

    public void addAttribute(final String name, final Object value) {
        sessions.put(name, value);
    }

    public String getId() {
        return id;
    }
}
