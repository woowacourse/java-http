package org.apache.catalina;

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

    public boolean hasAttribute(String id) {
        return values.containsKey(id);
    }

    public String getId() {
        return id;
    }

    public <T> void setAttribute(String key, T value) {
        values.put(key, value);
    }

    public <T> T getAttribute(String key) {
        if (values.containsKey(key)) {
            return (T) values.get(key);
        }
        return null;
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
