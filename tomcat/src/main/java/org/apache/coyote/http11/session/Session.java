package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.exception.SessionKeyNotFoundException;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public Object getAttribute(final String name) {
        if (!values.containsKey(name)) {
            throw new SessionKeyNotFoundException(name);
        }
        return values.get(name);
    }

    public String getId() {
        return id;
    }
}
