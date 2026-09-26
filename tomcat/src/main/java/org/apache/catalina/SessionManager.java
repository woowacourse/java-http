package org.apache.catalina;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private static final String COOKIE_ATTRIBUTES = "; Path=/; HttpOnly";
    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session create() {
        final Session session = new Session(UUID.randomUUID().toString());
        sessions.put(session.getId(), session);
        return session;
    }

    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public Optional<Session> findSession(final HttpRequest request) {
        return request.getCookie()
                .get(SESSION_COOKIE_NAME)
                .flatMap(this::findSession);
    }

    public void remove(final String id) {
        sessions.remove(id);
    }

    public String toCookie(final Session session) {
        return SESSION_COOKIE_NAME + "=" + session.getId() + COOKIE_ATTRIBUTES;
    }
}
