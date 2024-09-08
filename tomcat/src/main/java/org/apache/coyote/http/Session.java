package org.apache.coyote.http;

import com.techcourse.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private static final String USER = "user";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public static Session getSession() {
        return new Session(UUID.randomUUID().toString());
    }

    private Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public User getUser(final String name) {
        return (User) getAttribute(name);
    }

    public void setAttribute(final String name, final Object object) {
        values.put(name, object);
    }

    public void setUser(final User user) {
        values.put(USER, user);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }
}
