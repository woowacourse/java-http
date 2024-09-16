package org.apache.catalina.manager;

import com.techcourse.model.User;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.session.Session;

public class SessionManager implements Manager {
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static SessionManager instance;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean isSessionExist(String id) {
        return SESSIONS.containsKey(id);
    }

    public String generateSession(User user) {
        Session session = new Session(user);
        add(session);
        return session.getId();
    }
}
