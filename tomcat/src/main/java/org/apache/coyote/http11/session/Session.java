package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = createId();
    }

    private String createId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public Optional<Object> getAttribute(String name) {
        if (values.containsKey(name)) {
            return Optional.of(values.get(name));
        }
        return Optional.empty();
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        SessionManager.remove(id);
    }

    public String getId() {
        return id;
    }
}
