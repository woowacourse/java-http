package nextstep.jwp.http;

import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static void addSession(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public static HttpSession getSession(String id, User user) {
        if (!SESSIONS.containsKey(id)) {
            HttpSession httpSession = new HttpSession(id);
            httpSession.setAttribute("user", user);
            addSession(httpSession);
            return httpSession;
        }
        return SESSIONS.get(id);
    }

    public static boolean contains(String id) {
        return SESSIONS.containsKey(id);
    }
}
