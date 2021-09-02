package nextstep.jwp.http.message.element.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpSessions {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static Optional<Session> get(String uuid) {
        return Optional.ofNullable(SESSIONS.get(uuid));
    }

    public static void remove(String uuid) {
        SESSIONS.remove(uuid);
    }

    public static void put(Session httpSession) {
        SESSIONS.put(httpSession.getSessionId(), httpSession);
    }
}
