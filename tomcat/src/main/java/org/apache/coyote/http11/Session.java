package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public static Session create() {
        return new Session(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new IllegalArgumentException("세션 속성을 찾을 수 없습니다. : " + name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }
}
