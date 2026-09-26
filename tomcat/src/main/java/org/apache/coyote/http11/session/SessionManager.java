package org.apache.coyote.http11.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession session) {
        sessions.put(session.getId(), (Session) session);
    }

    @Override
    public Session findSession(String id) {
        if (id == null) {
            return null;
        }
        Session session = sessions.get(id);
        if (session != null) {
            session.access();
        }
        return session;
    }

    @Override
    public void remove(HttpSession session) {
        sessions.remove(session.getId());
    }
}
