package org.apache.coyote.http;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, User> SESSIONS = new ConcurrentHashMap<>();

    public static void add(String sessionId, User user) {
        SESSIONS.put(sessionId, user);
    }

    public static boolean isAlreadyLogin(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }
}
