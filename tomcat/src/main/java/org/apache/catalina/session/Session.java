package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(name))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setAttribute(String sessionId, Object value) {
        values.put(sessionId, value);
    }
}
