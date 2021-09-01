package nextstep.jwp.server;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.SessionNotFoundException;

public class HttpSessions {

    private final Map<String, HttpSession> sessions;


    public HttpSessions() {
        this(new HashMap<>());
    }

    public HttpSessions(Map<String, HttpSession> sessions) {
        this.sessions = sessions;
    }

    public void addSession(HttpSession httpSession) {
        sessions.put(httpSession.getId(), httpSession);
    }

    public Object findObject(String sessionId, String attributeName) {
        if (sessions.containsKey(sessionId)) {
            HttpSession httpSession = sessions.get(sessionId);
            return httpSession.getAttribute(attributeName);
        }

        throw new SessionNotFoundException();
    }
}
