package nextstep.jwp.server.http.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessions() {
    }

    public static void addSession(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }

    public static HttpSession findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }
}
