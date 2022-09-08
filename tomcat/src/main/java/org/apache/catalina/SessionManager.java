package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    @Override
    public void add(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    @Override
    public HttpSession findSession(String id) {
        HttpSession session = SESSIONS.get(id);
        if (session == null) {
            return new HttpSession();
        }
        return session;
    }

    @Override
    public void remove(HttpSession httpSession) {
        HttpSession findHttpSession = SESSIONS.values().stream()
                .filter(origin -> origin.equals(httpSession))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션의 ID입니다. : " + httpSession));

        SESSIONS.remove(findHttpSession.getId());
    }
}
