package org.apache.coyote.http11.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void add(final String id, final Session session) {
        SESSIONS.put(id, session);
    }

    public Session getById(final String jsessionid) {
        return SESSIONS.get(jsessionid);
    }
}
