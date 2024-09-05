package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, String> values = new HashMap<>();

    private Session(String id) {
        this.id = id;
    }

    public static Session createRandomSession() {
        return new Session(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String id) {
        return values.get(id);
    }

    public void setAttribute(String name, String value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
        ;
    }
}
