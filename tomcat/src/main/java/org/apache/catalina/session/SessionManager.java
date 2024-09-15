package org.apache.catalina.session;

import com.techcourse.model.domain.User;
import org.apache.catalina.Manager;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return new SessionManager();
    }

    public String findSessionId(final User user) {
        for (String key : SESSIONS.keySet()) {
            if (SESSIONS.get(key).getAttribute("user") == user) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
