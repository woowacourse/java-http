package org.apache.catalina.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public Object getAttribute(final String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new IllegalArgumentException("해당 세션 값은 존재하지 않습니다.");
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getValues() {
        return new HashMap<>(values);
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
        return Objects.hash(id);
    }
}
