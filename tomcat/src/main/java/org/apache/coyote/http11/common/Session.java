package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;

public class Session {
    private final String id;
    private final Map<String, User> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean hasAttribute(final String name) {
        return values.containsKey(name);
    }

    public User getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final User value) {
        values.put(name, value);
    }
}
