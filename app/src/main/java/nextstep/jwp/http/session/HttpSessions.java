package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    private HttpSessions() {}

    public static void register(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean contains(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public static boolean contains(HttpSession session) {
        return contains(session.getId());
    }
}
