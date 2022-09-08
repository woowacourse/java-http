package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(Session session) {
    }

    public static void addSession(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public static Session getSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {

    }
}
