package org.apache.coyote.http.session;

import org.apache.coyote.http.LoginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionManager implements LoginManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public boolean isAlreadyLogined(final String id) {
        if (Objects.nonNull(SESSIONS.get(id))) {
            return true;
        }

        return false;
    }
}
