package org.apache.catalina.session;

import org.apache.catalina.Manager;

import java.io.IOException;

public class SessionContext {

    private final Manager manager;
    private Session session;
    private boolean changed;

    public SessionContext(Manager manager, String sessionId) throws IOException {
        this.manager = manager;
        this.session = manager.findSession(sessionId);
        if (session == null) {
            session = createSession();
            changed = true;
        }
    }

    public Session getSession() {
        return session;
    }

    public Session renewSession() {
        Session newSession = createSession();
        manager.remove(session.getId());
        session = newSession;
        changed = true;
        return session;
    }

    public boolean isChanged() {
        return changed;
    }

    private Session createSession() {
        Session newSession = new Session();
        manager.add(newSession);
        return newSession;
    }
}
