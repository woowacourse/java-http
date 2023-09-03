package org.apache.coyote.http11.auth;

import java.util.HashMap;
import java.util.Map;

public class SessionRepositoryImpl implements SessionRepository {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void create(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session getSession(String id) {
        return SESSIONS.get(id);
    }

}
