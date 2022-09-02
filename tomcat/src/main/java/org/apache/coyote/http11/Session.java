package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session generate() {
        final UUID uuid = UUID.randomUUID();
        final String id = String.valueOf(uuid);

        return new Session(id);
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String key) {
        return values.get(key);
    }

    public void setAttribute(final String key, final Object value) {
        values.put(key, value);
    }
}
