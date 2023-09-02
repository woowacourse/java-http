package org.apache.coyote;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public void add(String sessionId, User user) {
        sessions.put(sessionId, user);
    }

    public boolean isAlreadyLogin(String sessionId) {
        return sessions.containsKey(sessionId);
    }

}
