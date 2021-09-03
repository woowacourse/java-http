package nextstep.jwp.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static final Logger log = LoggerFactory.getLogger(HttpSessions.class);
    private static final Map<String, HttpSession> sessions = new HashMap<>();

    private HttpSessions() {
    }

    public static void add(String id, HttpSession httpSession) {
        log.debug("session added : {}", id);
        sessions.put(id, httpSession);
    }

    public static HttpSession find(String sessionId) {
        log.debug("session added : {}", sessionId);
        return sessions.get(sessionId);
    }
}
