package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        checkExistence(id);
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        checkExistence(session.getId());
        SESSIONS.remove(session.getId());
    }

    private void checkExistence(String id) {
        SESSIONS.keySet().stream()
                .filter(key -> key.equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("id 에 해당하는 세션이 존재하지 않습니다."));
    }
}
