package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> items = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return items.get(name);
    }

    public void setAttribute(String name, Object value) {
        items.put(name, value);
    }

    public void removeAttribute(String name) {
        items.remove(name);
    }

    public void invalidate() {
        items.clear();
    }
}
