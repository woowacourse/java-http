package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        checkExistence(session.getId());
        SESSIONS.remove(session.getId());
    }

    public HttpSession createSession() {
        UUID uuid = UUID.randomUUID();
        HttpSession httpSession = new Session(uuid.toString());
        SESSIONS.put(uuid.toString(), httpSession);

        return httpSession;
    }

    private void checkExistence(String id) {
        boolean contains = SESSIONS.containsKey(id);
        if (!contains) {
            throw new IllegalArgumentException("id 에 해당하는 세션이 존재하지 않습니다.");
        }
    }
}
