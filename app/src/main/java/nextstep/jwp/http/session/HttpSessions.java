package nextstep.jwp.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void addSession(HttpSession httpSession) {
        SESSIONS.putIfAbsent(httpSession.getId(), httpSession);
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {
    }
}
