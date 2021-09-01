package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        HttpSession session = SESSIONS.get(id);
        if (Objects.isNull(session)) {
            session = new HttpSession(id);
            put(session);
            return session;
        }
        return session;
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static void put(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean contains(HttpSession session) {
        return SESSIONS.containsKey(session.getId());
    }
}
