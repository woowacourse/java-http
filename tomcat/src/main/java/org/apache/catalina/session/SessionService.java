package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

public class SessionService {

    private static final SessionGenerator DEFAULT_SESSION_GENERATOR = new UuidSessionGenerator();
    private static final Manager MANAGER = new SessionManager();
    private final SessionGenerator sessionGenerator;

    public SessionService() {
        this(DEFAULT_SESSION_GENERATOR);
    }

    public SessionService(SessionGenerator sessionGenerator) {
        this.sessionGenerator = sessionGenerator;
    }

    public HttpSession createSession() {
        return sessionGenerator.create();
    }

    public boolean isManagedSession(HttpSession session) {
        return findSession(session.getId()).isPresent();
    }

    public Optional<HttpSession> findSession(String sessionId) {
        return Optional.ofNullable(MANAGER.findSession(sessionId));
    }
}
