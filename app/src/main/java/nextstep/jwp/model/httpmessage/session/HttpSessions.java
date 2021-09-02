package nextstep.jwp.model.httpmessage.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {

    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void addSession(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean contains(String id) {
        return SESSIONS.containsKey(id);
    }
}
