package nextstep.jwp.http.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static HttpSession createSession() {
        HttpSession session = new HttpSession(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public static void clear() {
        SESSIONS.clear();
    }

    public static int getSessionSize() {
        return SESSIONS.size();
    }

    public static Set<String> getSessionIds() {
        return SESSIONS.keySet();
    }

}

