package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        final Session registeredSession = SESSIONS.get(id);
        if (registeredSession == null)  {
            final Session newSession  = new Session(id);
            SESSIONS.put(id, newSession);
            return newSession;
        }
        return registeredSession;
    }

    private SessionManager() {}
}
