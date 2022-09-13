package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this(UUID.randomUUID().toString());
    }

    public Session(final String id) {
        this.id = id;
    }

    public Optional<Object> getAttribute(final String name) {
        if (values.containsKey(name)) {
            return Optional.of(values.get(name));
        }
        return Optional.empty();
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }
}
