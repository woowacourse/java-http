package org.apache.coyote.http11.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void add(Session session) {
        SESSIONS.putIfAbsent(session.getId(), session);
    }

    public Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

}
