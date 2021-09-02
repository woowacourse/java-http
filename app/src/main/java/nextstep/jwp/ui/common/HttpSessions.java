package nextstep.jwp.ui.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {
    private static Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    public static void addSession(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }

    public static HttpSession findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }
}
