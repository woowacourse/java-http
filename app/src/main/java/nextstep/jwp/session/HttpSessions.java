package nextstep.jwp.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String sessionId) {
        if (!SESSIONS.containsKey(sessionId)) {
            HttpSession session = new HttpSession(sessionId);
            SESSIONS.put(sessionId, session);
            return session;
        }
        return SESSIONS.get(sessionId);
    }
}
