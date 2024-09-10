package org.apache.catalina.manager;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(Session session) {

    }

    @Override
    public Session findSession(String id) throws IOException {
        return null;
    }

    @Override
    public void remove(Session session) {

    }
}
