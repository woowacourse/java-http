package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(String id) {
        this.id = id;
        values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public boolean hasValue(String key) {
        return values.containsKey(key);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
