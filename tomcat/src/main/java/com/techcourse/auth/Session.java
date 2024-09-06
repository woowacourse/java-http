package com.techcourse.auth;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class Session {

    @Getter
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
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

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}
