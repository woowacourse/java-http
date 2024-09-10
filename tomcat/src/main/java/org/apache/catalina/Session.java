package org.apache.catalina;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private static final String USER_SESSION_NAME = "user";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this(UUID.randomUUID().toString());
    }

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public User getUserAttribute() {
        return (User) values.get(USER_SESSION_NAME);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public void setUserAttribute(User user) {
        values.put("user", user);
    }

}
