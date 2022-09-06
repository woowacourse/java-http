package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.domain.model.User;

public class SessionManager {

    private static final SessionManager instance = new SessionManager(new HashMap<>());

    private final Map<String, User> sessions;

    private SessionManager(final Map<String, User> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public String addSession(final User user) {
        final String jSessionId = UUID.randomUUID().toString();
        sessions.put(jSessionId, user);

        return jSessionId;
    }

    public Optional<User> findSession(final String jSessionId) {
        return Optional.ofNullable(sessions.get(jSessionId));
    }

    public void clear() {
        sessions.clear();
    }
}
