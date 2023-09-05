package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(final String id, final Session session) {
        SESSIONS.put(id, session);
    }

    public Session getById(final String jsessionid) {
        return SESSIONS.get(jsessionid);
    }
}
