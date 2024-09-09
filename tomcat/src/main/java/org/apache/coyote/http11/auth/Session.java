package org.apache.coyote.http11.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {
    private static final IdGenerator idGenerator = new IdGenerator();

    private final String id;
    private final Map<String, Object> attributes;

    public Session() {
        this.id = idGenerator.generate();
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
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
        return Objects.equals(getId(), session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
