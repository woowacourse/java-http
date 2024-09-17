package org.apache.catalina.servlet.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> session = new ConcurrentHashMap<>();

    private static volatile SessionManager instance;

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    private SessionManager() {}

    @Override
    public void add(Session session) {
        SessionManager.session.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
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
