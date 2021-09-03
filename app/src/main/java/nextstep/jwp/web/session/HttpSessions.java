package nextstep.jwp.web.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession() {
        String sessionId = UUID.randomUUID().toString();
        return new HttpSession(sessionId);
    }

    public static void addSession(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
