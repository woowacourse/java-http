package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session() {
        this.id = createRandomUUID();
        this.values = new HashMap<>();
    }

    private String createRandomUUID() {
        final UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        if (values.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Duplicated Session values: %s", name));
        }
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        //TODO
    }
}
