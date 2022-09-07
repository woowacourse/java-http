package org.apache.coyote.http11.session;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager instance = new SessionManager(new ConcurrentHashMap<>());

    private static final long DEFAULT_TIMEOUT = 1800000;

    private final ConcurrentHashMap<String, Long> sessions;

    private SessionManager(final ConcurrentHashMap<String, Long> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public String addSession(final Long userId) {
        final String jSessionId = UUID.randomUUID().toString();
        setSessionTimeout(jSessionId, DEFAULT_TIMEOUT);
        sessions.put(jSessionId, userId);

        return jSessionId;
    }

    public String addSession(final Long userId, final long timeout) {
        final String jSessionId = UUID.randomUUID().toString();
        setSessionTimeout(jSessionId, timeout);
        sessions.put(jSessionId, userId);

        return jSessionId;
    }

    private void setSessionTimeout(final String jSessionId, final long timeout) {
        final Thread sessionRemoveThread = new Thread(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException("세션 타임아웃 설정 실패", e);
            }

            sessions.remove(jSessionId);
        });

        sessionRemoveThread.start();
    }

    public Optional<Long> findSession(final String jSessionId) {
        return Optional.ofNullable(sessions.get(jSessionId));
    }

    public void clear() {
        sessions.clear();
    }
}
