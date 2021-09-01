package nextstep.joanne.server.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession(String id) {
        HttpSession httpSession = SESSIONS.get(id);
        if (Objects.isNull(httpSession)) {
            httpSession = new HttpSession(id);
            put(httpSession);
            return httpSession;
        }
        return httpSession;
    }

    private static void put(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static boolean get(HttpSession session) {
        return SESSIONS.containsKey(session.getId());
    }
}
