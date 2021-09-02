package nextstep.jwp.framework.http.common;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static void addSession(final String sessionId, final HttpSession httpSession) {
        SESSIONS.put(sessionId, httpSession);
    }

    public static HttpSession getSession(final String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public static void remove(final String sessionId) {
        SESSIONS.remove(sessionId);
    }

    private HttpSessions() {
    }
}
