package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public void add(String key, Object object) {
        attributes.put(key, object);
    }

    public String getId() {
        return id;
    }

    public Object get(String key) {
        return attributes.get(key);
    }

    public void invalidate() {
        attributes.clear();
    }

}
