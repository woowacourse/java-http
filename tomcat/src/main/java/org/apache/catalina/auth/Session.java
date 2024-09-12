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

    public Object getAttribute(final String id) {
        if (values.containsKey(id)) {
            return values.get(id);
        }
        throw new IllegalStateException("해당 세션 값은 존재하지 않습니다.");
    }

    public void setAttribute(final String id, final Object value) {
        values.put(id, value);
    }

    public void removeAttribute(final String id) {
        values.remove(id);
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
