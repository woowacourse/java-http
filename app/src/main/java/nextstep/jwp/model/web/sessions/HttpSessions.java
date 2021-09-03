package nextstep.jwp.model.web.sessions;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static void addSession(String sessionId, HttpSession session) {
        SESSIONS.put(sessionId, session);
    }

    public static boolean existsSessionId(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }
}
