package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session() {
        this.id = createId();
    }

    private String createId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }
}
