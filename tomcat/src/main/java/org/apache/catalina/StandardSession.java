package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class StandardSession implements Session {

    private final String id;
    private final Manager manager;
    private final Map<String, Object> values = new HashMap<>();

    private boolean valid = true;

    StandardSession(final String id) {
        this(id, SessionManager.getInstance());
    }

    StandardSession(final String id, final Manager manager) {
        this.id = Objects.requireNonNull(id);
        this.manager = Objects.requireNonNull(manager);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(final String name) {
        validate();
        return values.get(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        validate();
        Objects.requireNonNull(name);
        if (value == null) {
            removeAttribute(name);
            return;
        }
        values.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        validate();
        values.remove(name);
    }

    @Override
    public void invalidate() {
        validate();
        values.clear();
        manager.remove(this);
        valid = false;
    }

    private void validate() {
        if (!valid) {
            throw new IllegalStateException("Session has already been invalidated");
        }
    }
}
