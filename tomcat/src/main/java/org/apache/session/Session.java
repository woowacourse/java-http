package org.apache.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public void createAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public Object readAttribute(final String name) {
        return values.get(name);
    }

    public void updateAttribute(final String name, final Object value) {
        values.replace(name, value);
    }

    public void deleteAttribute(final String name) {
        values.remove(name);
    }

    public String getId() {
        return id;
    }
}
