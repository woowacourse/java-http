package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static boolean existSessionId(String id) {
        return SESSIONS.containsKey(id);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static HttpSession createSession() {
        String sessionId = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(sessionId);
        SESSIONS.put(sessionId, httpSession);

        return httpSession;
    }
}
