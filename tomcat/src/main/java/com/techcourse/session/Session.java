package com.techcourse.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    public static final String SESSION_KEY = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();


    public Session() {
        this.id = UUID.randomUUID().toString();
        setAttribute(SESSION_KEY, id);
    }

    public String getId() {
        return this.id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, String value) {
        values.put(name, value);
    }
}
