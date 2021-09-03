package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void save(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static boolean exist(String id) {
        return SESSIONS.containsKey(id);
    }

    private HttpSessions() {
    }
}
