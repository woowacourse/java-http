package org.apache.coyote.http11.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public SessionManager() {
    }

    public void add(final HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public HttpSession findSession(final String id) throws IOException {
        return SESSIONS.get(String.valueOf(id));
    }

    public void remove(final HttpSession httpSession) {
    }
}
