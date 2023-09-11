package org.apache.catalina.startup;

import ch.qos.logback.core.spi.LifeCycle;
import common.http.Session;
import org.apache.catalina.Manager;

public class SessionManager implements Manager, LifeCycle {

    private static final Sessions sessions = new Sessions();
    private boolean isStarted = false;

    public SessionManager() {
        start();
    }

    @Override
    public void add(Session session) {
        sessions.add(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessions.has(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }

    @Override
    public void start() {
        if (!isStarted) {
            isStarted = true;
        }
    }

    @Override
    public void stop() {
        if (isStarted) {
            sessions.clear();
            isStarted = false;
        }
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    public boolean hasSession(Session session) {
        return sessions.has(session);
    }
}
