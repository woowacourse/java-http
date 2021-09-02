package nextstep.jwp.web.network;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final HttpSession DEFAULT_SESSION = null;

    private static final Map<String, HttpSession> sessions = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession(String id) {
        return sessions.getOrDefault(id, DEFAULT_SESSION);
    }

    public static void setSession(String id) {
        final HttpSession session = new HttpSession(id);
        setSession(session);
    }

    public static void setSession(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    public static void remove(String id) {
        sessions.remove(id);
    }

    public static boolean doesNotContain(String sessionId) {
        return !sessions.containsKey(sessionId);
    }
}
