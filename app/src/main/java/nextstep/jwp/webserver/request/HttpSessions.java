package nextstep.jwp.webserver.request;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private final Map<String, HttpSession> sessions = new HashMap<>();

    public HttpSession getSession(String id) {
        return sessions.computeIfAbsent(id, key -> new HttpSession());
    }
}
