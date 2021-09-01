package nextstep.jwp.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private Sessions() {
    }

    public static Session getSession(Cookie cookie) {
        String id = cookie.getSessionId();
        return SESSIONS.computeIfAbsent(id, k -> new Session(id));
    }
}
