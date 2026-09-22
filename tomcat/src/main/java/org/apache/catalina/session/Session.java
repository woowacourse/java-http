package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    private Session(String id,  Map<String, Object> values) {
        this.id = id;
        this.values = values;
    }

    public static Session from(String id) {
        return new Session(id, new ConcurrentHashMap<>());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        if (value == null) {
            values.remove(name);
            return;
        }

        values.put(name, value);
    }
}
