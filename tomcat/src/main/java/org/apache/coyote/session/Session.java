package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session empty() {
        return new Session(null);
    }

    public Object getAttribute(final String name) {
        return values.getOrDefault(name, null);
    }

    public void setAttribute(final String name, final String value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public String id() {
        return id;
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

    @Override
    public String toString() {
        return "Session{" +
               "id='" + id + '\'' +
               ", values=" + values +
               '}';
    }
}
