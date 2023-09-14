package org.apache.catalina.session;

import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    // static!
    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession httpSession) {
        sessions.put(httpSession.getId(), httpSession);
    }

    @Override
    public HttpSession findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession httpSession) {
        sessions.remove(httpSession.getId());
    }

    public static boolean isExist(final String id) {
        return sessions.containsKey(id);
    }

}
