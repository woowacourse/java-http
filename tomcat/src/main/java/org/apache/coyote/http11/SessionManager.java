package org.apache.coyote.http11;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public static void add(String sessionId, User user) {
        sessions.put(sessionId, user);
    }

    public static boolean isAlreadyLogin(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
