package com.techcourse.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    public static final String SESSION_KEY = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();


    public Session() {
        this.id = generateRandomId();
        setAttribute(SESSION_KEY, id);
    }

    private static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }

    public synchronized Object getAttribute(String name) {
        return values.get(name);
    }

    public synchronized void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public boolean hasSameIdWith(String otherId) {
        return this.id.equals(otherId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
