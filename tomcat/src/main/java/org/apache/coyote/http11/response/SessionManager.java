package org.apache.coyote.http11.response;

import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();
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


    private static void add(Session session) {
        sessions.put(session.getId(), session);
    }
}
