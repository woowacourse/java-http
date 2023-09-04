package org.apache.catalina;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession session) {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return sessionMap.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        sessionMap.remove(session.getId());
    }

}
