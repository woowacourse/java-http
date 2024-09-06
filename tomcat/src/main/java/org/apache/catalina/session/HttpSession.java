package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values;

    public HttpSession(String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public String getId() {
        return id;
    }
}
