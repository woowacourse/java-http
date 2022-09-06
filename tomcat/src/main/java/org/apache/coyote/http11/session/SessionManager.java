package org.apache.coyote.http11.session;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.domain.model.User;

public class SessionManager {

    private static final SessionManager instance = new SessionManager(new ConcurrentHashMap<>());

    private static final long DEFAULT_TIMEOUT = 1800000;

    private final ConcurrentHashMap<String, User> sessions;

    private SessionManager(final ConcurrentHashMap<String, User> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public String addSession(final User user) {
        final String jSessionId = UUID.randomUUID().toString();
        setSessionTimeout(jSessionId, DEFAULT_TIMEOUT);
        sessions.put(jSessionId, user);

        return jSessionId;
    }

    public String addSession(final User user, final long timeout) {
        final String jSessionId = UUID.randomUUID().toString();
        setSessionTimeout(jSessionId, timeout);
        sessions.put(jSessionId, user);

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

    public Optional<User> findSession(final String jSessionId) {
        return Optional.ofNullable(sessions.get(jSessionId));
    }

    public void clear() {
        sessions.clear();
    }
}
