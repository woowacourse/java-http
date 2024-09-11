package org.apache.catalina.controller.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> session = new HashMap<>();

    private static SessionManager instance;

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    private SessionManager() {}

    @Override
    public void add(Session session) {
        SessionManager.session.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws IOException {
        return session.get(id);
    }

    @Override
    public void remove(Session session) {
        SessionManager.session.remove(session.getId());
    }


    public boolean hasSession(String sessionId) {
        return session.containsKey(sessionId);
    }
}
