package nextstep.jwp.framework.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    private HttpSessions() {
    }

    public static void add(String id, HttpSession httpSession) {
        sessions.put(id, httpSession);
    }

    public static Optional<HttpSession> find(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public static void remove(String id) {
        sessions.remove(id);
    }
}
