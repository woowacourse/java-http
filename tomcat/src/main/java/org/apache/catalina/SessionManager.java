package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static ResolvedSession resolve(String sessionId) {
        if (sessionId != null) {
            Session session = SESSIONS.get(sessionId);
            if (session != null) {
                return ResolvedSession.found(session);
            }
        }

        Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return ResolvedSession.created(session);
    }
}
