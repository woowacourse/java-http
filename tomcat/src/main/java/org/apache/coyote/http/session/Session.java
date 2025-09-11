package org.apache.coyote.http.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session newSession() {
        return new Session(String.valueOf(UUID.randomUUID()));
    }

    public String getId() {
        return id;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
