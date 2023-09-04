package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;

public class Session {
    private Map<String, User> sessions;

    public Session(Map<String, User> sessions) {
        this.sessions = sessions;
    }

    public User getUser(String key) {
        return sessions.get(key);
    }

    public String addUser(User value) {
        final var key = UUID.randomUUID().toString();
        sessions.put(key, value);
        return key;
    }

}
