package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        if (isValid(id)) {
            return SESSIONS.get(id);
        }
        return createSession();
    }

    public static void putSession(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public static HttpSession createSession() {
        return new HttpSession(UUID.randomUUID().toString());
    }

    public static boolean isValid(String id) {
        return SESSIONS.containsKey(id);
    }

    public void remove(String id) {
        SESSIONS.remove(id);
    }
}
