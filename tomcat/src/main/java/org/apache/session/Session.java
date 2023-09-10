package org.apache.session;

import java.util.*;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session create(){
        return new Session(UUID.randomUUID().toString());
    }

    public static Session create(final String id) {
        return new Session(id);
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
        values.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
