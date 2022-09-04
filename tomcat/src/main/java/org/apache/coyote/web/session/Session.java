package org.apache.coyote.web.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        SessionManager.remove(id);
    }

    public Map<String, Object> getValues() {
        return values;
    }
}
