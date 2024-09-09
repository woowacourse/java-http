package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionManger implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
