package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private UUID id;
    private static final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID();
    }

    public Session setAttribute(final String name, final Object value) {
        values.put(name, value);
        return this;
    }

    public String getId() {
        return id.toString();
    }
}
