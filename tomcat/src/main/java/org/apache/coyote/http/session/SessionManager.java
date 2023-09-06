package org.apache.coyote.http.session;

import org.apache.coyote.http.LoginManager;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements LoginManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public boolean isAlreadyLogined(final String id) {
        return SESSIONS.get(id) != null;
    }
}
