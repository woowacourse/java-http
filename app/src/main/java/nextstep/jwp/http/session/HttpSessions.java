package nextstep.jwp.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();


    public static HttpSession getSession(String id) {
        return SESSIONS.computeIfAbsent(id, HttpSession::new);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {}
}
