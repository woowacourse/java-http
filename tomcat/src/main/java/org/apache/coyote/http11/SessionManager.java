package org.apache.coyote.http11;

import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private SessionManager() {
    }

    private static final Map<String, User> sessions = new HashMap<>();

    public static void addSession(String sessionId, User user) {
        sessions.put(sessionId, user);
    }

    public static User getUser(String sessionId) {
        return sessions.get(sessionId);
    }

    public static boolean containsKey(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
