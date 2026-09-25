package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {

    }

    public static Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public static Session getSession(final String id) {
        if (id != null) {
            Session session = SESSIONS.get(id);

            if (session != null) {
                return session;
            }
        }

        return createSession();
    }
}
