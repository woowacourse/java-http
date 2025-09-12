package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static Session createNew() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        SESSIONS.put(uuid.toString(), session);
        return session;
    }

    public static Session findSession(String id) {
        if(SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        return createNew();
    }

    public static void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}
