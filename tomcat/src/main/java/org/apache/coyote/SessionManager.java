package org.apache.coyote;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private SessionManager() {
        throw new IllegalArgumentException("인스턴스를 생성할 수 없습니다.");
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

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
        SESSIONS.remove(session.getId());
    }
}
