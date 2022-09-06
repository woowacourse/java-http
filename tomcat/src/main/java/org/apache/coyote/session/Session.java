package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Optional<Object> getAttribute(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
