package org.apache.coyote.http11.session;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager(new ConcurrentHashMap<>());

    private static final long CLEAR_GAP_MILLIS = 1_800_000;

    private final ConcurrentHashMap<String, Session> sessions;

    private SessionManager(final ConcurrentHashMap<String, Session> sessions) {
        setExpiredCleaner();
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public void setExpiredCleaner() {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                sessions.values()
                        .stream()
                        .filter(Session::isExpired)
                        .forEach(session -> sessions.remove(session.getId()));
            }
        };

        timer.scheduleAtFixedRate(timerTask, CLEAR_GAP_MILLIS, CLEAR_GAP_MILLIS);
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        final Session session = sessions.get(id);
        if (session == null) {
            return Optional.empty();
        }
        if (session.isExpired()) {
            sessions.remove(session.getId());
            return Optional.empty();
        }
        return Optional.of(session);
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    public void clear() {
        sessions.clear();
    }
}
