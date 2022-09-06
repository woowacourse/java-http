package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        validateContainsAttribute(name);
        return values.get(name);
    }

    private void validateContainsAttribute(String name) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("No Such Attribute exists in this Session");
        }
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
