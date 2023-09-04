package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;

public class Session {
    private final Map<String, User> sessions;

    public Session() {
        this.sessions = new HashMap<>();
    }

    public User getUser(String key) {
        return sessions.get(key);
    }

    public String addUser(User user) {
        final var key = UUID.randomUUID().toString();
        sessions.put(key, user);
        return key;
    }

    public boolean exists(String key) {
        return sessions.containsKey(key);
    }

}
