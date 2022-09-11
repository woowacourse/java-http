package org.apache.catalina;

import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setAttribute(final String name, final Object value) {
        this.values.put(name, value);
    }
}
