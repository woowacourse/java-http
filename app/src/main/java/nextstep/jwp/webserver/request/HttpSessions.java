package nextstep.jwp.webserver.request;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private Map<String, HttpSession> sessions = new HashMap<>();

    public HttpSession getSession(String id) {
        final HttpSession httpSession = sessions.computeIfAbsent(id, key -> new HttpSession());
        return httpSession;
    }
}
