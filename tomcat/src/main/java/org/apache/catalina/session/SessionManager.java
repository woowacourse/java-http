package org.apache.catalina.session;

import ch.qos.logback.core.spi.LifeCycle;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager implements Manager, LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final SessionManager instance = new SessionManager();
    private static final Map<String, Session> sessions = new HashMap<>();
    private static boolean isStarted;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final String id) {
        sessions.remove(id);
    }

    @Override
    public void start() {
        if (isStarted) {
            return;
        }
        isStarted = true;

        log.info("SessionManager has started.");
    }

    @Override
    public void stop() {
        if (!isStarted) {
            return;
        }
        isStarted = false;
        sessions.clear();

        log.info("SessionManager has stopped.");
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
