package org.apache.coyote.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";
    private final String id;
    private final Map<String, Object> stores = new HashMap<>();

    public Session(final String id) {
        if (id.contains(JSESSIONID)) {
            this.id = id.replaceFirst(JSESSIONID + "=", "");
            return;
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String text() {
        return JSESSIONID + "=" + id;
    }

    public Object getAttribute(final String name) {
        return stores.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        stores.put(name, value);
    }

    public void removeAttribute(final String name) {
        stores.remove(name);
    }

    public void invalidate() {
        stores.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
