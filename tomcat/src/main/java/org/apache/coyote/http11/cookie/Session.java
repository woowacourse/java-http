package org.apache.coyote.http11.cookie;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
