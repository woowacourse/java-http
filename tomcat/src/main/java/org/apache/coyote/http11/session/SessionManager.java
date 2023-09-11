package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public enum SessionManager implements Manager {

    INSTANCE;

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
