package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession httpSession) {
        sessions.put(httpSession.getId(), httpSession);
    }

    @Override
    public HttpSession findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession httpSession) {
        sessions.remove(httpSession.getId());
    }
}
