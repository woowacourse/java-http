package nextstep.jwp.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession(String id) {
        if (id != null && SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        return SESSIONS.put(id, new HttpSession(id));
    }

    public static void put(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }

    public static int size() {
        return SESSIONS.size();
    }

    public static void clear() {
        SESSIONS.clear();
    }
}
