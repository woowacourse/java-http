package org.apache.coyote.http11.session;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, User> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public User getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final User value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public String getId() {
        return id;
    }

    public void invalidate() {
        values.clear();
    }
}
