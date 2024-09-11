package org.apache.coyote.http.session;

import org.apache.catalina.Manager;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionManager implements Manager {

    public static HttpSessionManager getInstance() {
        return new HttpSessionManager();
    }

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessionManager() {
    }

    @Override
    public void add(final HttpSession session) {
        SESSIONS.put(session.getId().toString(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
