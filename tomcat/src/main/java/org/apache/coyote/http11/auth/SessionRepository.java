package org.apache.coyote.http11.auth;

import java.util.HashMap;
import java.util.Map;

public class SessionRepository {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void create(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session getSession(String id) {
        return SESSIONS.get(id);
    }

}
