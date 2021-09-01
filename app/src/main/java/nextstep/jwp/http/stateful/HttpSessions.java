package nextstep.jwp.http.stateful;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static void addSession(String sessionId, HttpSession httpSession) {
        SESSIONS.put(sessionId, httpSession);
    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static boolean existSession(String id){
        return SESSIONS.containsKey(id);
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

}
