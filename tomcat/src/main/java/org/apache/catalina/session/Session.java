package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        final UUID randomId = UUID.randomUUID();
        this.id = randomId.toString();
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }
}
