package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        throw new NoSuchElementException("해당 세션이 존재하지 않습니다.");
    }

    public static void add(String id) {
        SESSIONS.put(id, new HttpSession(id));
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {}
}
