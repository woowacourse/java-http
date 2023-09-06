package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    private boolean isValid;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>();
        this.isValid = true;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        checkValidity();
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        checkValidity();
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        checkValidity();
        values.remove(name);
    }

    public void invalidate() {
        this.isValid = false;
    }

    private void checkValidity() {
        if (!isValid) {
            throw new InvalidSessionException();
        }
    }
}
