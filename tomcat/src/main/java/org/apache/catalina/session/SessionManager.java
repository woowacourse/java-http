package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final HttpSession session) {
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) throws IOException {
        return null;
    }

    public static Session getSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {}
}