package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public String create(String key, Object value) {
        Session session = new Session();
        session.setAttribute(key, value);
        SESSIONS.put(session.getId(), session);
        return session.getId();
    }

    public boolean contains(String id) {
        return SESSIONS.containsKey(id);
    }
}
