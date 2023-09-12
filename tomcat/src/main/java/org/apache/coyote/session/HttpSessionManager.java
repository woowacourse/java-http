package org.apache.coyote.session;

import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpSessionManager {

    private final SessionManager sessionManager = SessionManager.getInstance();

    public Session addNewSession() {
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }

    public Session findSession(String id) {
        return sessionManager.findSession(id);
    }


    public void remove(Session session) {
        sessionManager.remove(session);
    }
}
