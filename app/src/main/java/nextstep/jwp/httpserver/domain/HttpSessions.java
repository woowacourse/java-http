package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        return new HttpSession(UUID.randomUUID().toString());
    }

    public static void save(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {
    }
}
