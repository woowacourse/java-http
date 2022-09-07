package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;

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
        return values.get(name);
    }

    public void setAttribute(String key, User user) {
        values.put(key, user);
    }
}
