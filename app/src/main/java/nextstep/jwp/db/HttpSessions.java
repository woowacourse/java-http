package nextstep.jwp.db;

import nextstep.jwp.web.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    private HttpSessions() {}

    public static HttpSession issueSession() {
        HttpSession session = new HttpSession(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public static boolean isValidSession(HttpSession session) {
        return SESSIONS.containsKey(session.getId());
    }
}
