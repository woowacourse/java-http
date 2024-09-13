package org.apache.coyote.http11.session;

import com.techcourse.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final String SESSION_USER_NAME = "user";

    private final String id;
    private final Map<String, Object> attributes;

    public Session(String id) {
        this.id = id;
        this.attributes = new ConcurrentHashMap<>();
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public boolean hasUser() {
        return hasAttribute(SESSION_USER_NAME);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Object getUser() {
        return getAttribute(SESSION_USER_NAME);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void setUser(User user) {
        setAttribute(SESSION_USER_NAME, user);
    }

    public String getId() {
        return id;
    }
}
