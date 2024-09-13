package org.apache.coyote.http11;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public boolean hasSession(String sessionId) {
        return sessions.keySet().contains(sessionId);
    }

    public void putSession(String sessionId, Session session) {
        sessions.put(sessionId, session);
    }

    public UUID putUserSession(User user) {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId.toString());
        session.setAttributes("user", user);

        sessions.put(sessionId.toString(), session);
        return sessionId;
    }
}
