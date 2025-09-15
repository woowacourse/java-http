package org.apache.coyote.http11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    // key - JSESSION_ID, value - SESSION
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws IOException {
        Session session = SESSIONS.get(id);
        if (session == null) {
            throw new IllegalArgumentException("[ERROR] session not found");
        }
        return session;
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean isExistSessionId(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public SessionManager(){}
}
