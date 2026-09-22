package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.time.Clock;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final long SESSION_LIFETIME_MILLIS = Duration.ofMinutes(30).toMillis();

    private final Map<String, ManagedSession> sessions = new ConcurrentHashMap<>();
    private final Clock clock;

    public SessionManager() {
        this(Clock.systemUTC());
    }

    public SessionManager(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void add(HttpSession session) {
        removeExpiredSessions();
        sessions.put(session.getId(), new ManagedSession(session, clock.millis()));
    }

    @Override
    public HttpSession findSession(String id) {
        removeExpiredSessions();
        final ManagedSession managed = id == null ? null : sessions.get(id);
        return managed == null ? null : managed.session();
    }

    @Override
    public void remove(HttpSession session) {
        final ManagedSession managed = sessions.get(session.getId());
        if (managed != null && managed.session() == session) {
            sessions.remove(session.getId(), managed);
        }
    }

    private void removeExpiredSessions() {
        final long now = clock.millis();
        sessions.forEach((id, managed) -> {
            if (now - managed.registeredAt() >= SESSION_LIFETIME_MILLIS) {
                synchronized (managed.session()) {
                    if (sessions.remove(id, managed)) {
                        managed.session().invalidate();
                    }
                }
            }
        });
    }

    private record ManagedSession(HttpSession session, long registeredAt) {
    }
}
