package org.apache.catalina.session;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final String SESSION_COOKIE = "JSESSIONID";

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public Session findSession(HttpRequest request) {
        return request.getCookie(SESSION_COOKIE)
                .map(this::findSession)
                .orElse(null);
    }

    public Session replaceSession(HttpRequest request, HttpResponse response) {
        Session session = findSession(request);
        if (session != null) {
            session.invalidate();
        }

        Session newSession = new Session(UUID.randomUUID().toString());
        add(newSession);
        response.setHeader("Set-Cookie", SESSION_COOKIE + "=" + newSession.getId() + "; Path=/");
        return newSession;
    }

    public void invalidate(HttpRequest request, HttpResponse response) {
        if (request.getCookie(SESSION_COOKIE).isEmpty()) {
            return;
        }

        Session session = findSession(request);
        if (session != null) {
            session.invalidate();
        }
        response.setHeader("Set-Cookie", SESSION_COOKIE + "=; Max-Age=0; Path=/");
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }
}
