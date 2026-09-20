package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {
//Http11Processor는 요청마다 새로 만들어지지만,
// 세션 데이터는 요청이 끝나도 살아 있어야 하기 때문에 stataic
    private static final SessionManager INSTANCE =
            new SessionManager();

    private static final Map<String, HttpSession> SESSIONS =
            new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        final String id =
                UUID.randomUUID().toString();

        final Session session =
                new Session(id);

        add(session);

        return session;
    }

    @Override
    public void add(final HttpSession session) {
        SESSIONS.put(
                session.getId(),
                session
        );
    }

    @Override
    public HttpSession findSession(final String id) {
        if (id == null) {
            return null;
        }

        final HttpSession session =
                SESSIONS.get(id);

        if (session instanceof Session foundSession) {
            foundSession.access();
        }

        return session;
    }

    @Override
    public void remove(final HttpSession session) {
        if (session == null) {
            return;
        }

        SESSIONS.remove(
                session.getId()
        );
    }
}