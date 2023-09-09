package org.apache.coyote.http11.response;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final String USER = "user";

    private SessionManager() {
    }

    public static String getSessionId(User user) {
        String sessionId = sessions.keySet().stream()
                .filter(key -> sessions.get(key).getAttribute(USER).equals(user))
                .findAny()
                .orElse(UUID.randomUUID().toString());

        Session session = new Session(sessionId);
        session.setAttribute(USER, user);
        add(session);

        return sessionId;
    }

    public static Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    private static void add(Session session) {
        sessions.putIfAbsent(session.getId(), session);
    }
}
