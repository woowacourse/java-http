package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

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
        SESSIONS.keySet().stream()
                .filter(key -> key.equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("id 에 해당하는 세션이 존재하지 않습니다."));
    }
}
