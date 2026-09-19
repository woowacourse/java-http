package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private String id;
    private Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public Object getAttribute(String key) {
        return values.get(key);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }

}
