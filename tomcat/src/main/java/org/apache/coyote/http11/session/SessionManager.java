package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public enum SessionManager implements Manager {

    INSTANCE;

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession httpSession) {
        SESSIONS.remove(httpSession.getId());
    }

}
