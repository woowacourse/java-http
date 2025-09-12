package org.apache.coyote.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class HttpSessionManager implements Manager {
    private static final HttpSessionManager INSTANCE = new HttpSessionManager();

    private final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessionManager() {
    }

    public static HttpSessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public boolean containsKey(String id) {
        return SESSIONS.containsKey(id);
    }
}

