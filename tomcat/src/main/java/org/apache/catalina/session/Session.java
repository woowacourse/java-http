package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final Map<String, Object> attributes;

    private Session(String id,  Map<String, Object> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public static Session from(String id) {
        return new Session(id, new ConcurrentHashMap<>());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        if (value == null) {
            attributes.remove(name);
            return;
        }

        attributes.put(name, value);
    }
}
