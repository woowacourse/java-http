package org.apache.coyote.http;

import com.techcourse.model.User;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
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

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }
}
