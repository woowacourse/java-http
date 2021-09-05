package nextstep.joanne.server.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void update(String id, HttpSession httpSession) {
        SESSIONS.putIfAbsent(id, httpSession);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static boolean get(HttpSession session) {
        return SESSIONS.containsKey(session.getId());
    }

    public static void addSession(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }
}
