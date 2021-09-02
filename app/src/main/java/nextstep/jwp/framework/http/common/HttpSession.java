package nextstep.jwp.framework.http.common;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String sessionId;
    private final Map<String, Object> values = new HashMap<>();

    private HttpSession(final String sessionId) {
        this.sessionId = sessionId;
    }

    public static HttpSession of(String sessionId) {
        final HttpSession httpSession = new HttpSession(sessionId);
        HttpSessions.addSession(sessionId, httpSession);
        return httpSession;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(this.sessionId);
    }

    public String getSessionId() {
        return sessionId;
    }
}
