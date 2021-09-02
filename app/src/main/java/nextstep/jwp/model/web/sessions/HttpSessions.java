package nextstep.jwp.model.web.sessions;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void addSession(String sessionId, HttpSession session) {
        SESSIONS.put(sessionId, session);
    }

    public static boolean existsSessionId(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
