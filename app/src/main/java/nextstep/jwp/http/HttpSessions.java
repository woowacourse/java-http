package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.entity.HttpSession;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        HttpSession httpSession = SESSIONS.get(id);
        if (httpSession == null) {
            httpSession = new HttpSession(id);
            SESSIONS.put(id, httpSession);
        }
        return httpSession;
    }

    public static void add(String id, HttpSession session) {
        SESSIONS.put(id, session);
    }

    public static void remove(String id){
        SESSIONS.remove(id);
    }

    public static boolean containsKey(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public static void clear() {
        SESSIONS.clear();
    }
}
