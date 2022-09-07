package org.apache.coyote.http11.support.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", values=" + values +
                '}';
    }
}
