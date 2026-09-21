package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {
    //Http11Processor는 요청마다 새로 만들어지지만,
// 세션 데이터는 요청이 끝나도 살아 있어야 하기 때문에 stataic
    private static final SessionManager INSTANCE =
            new SessionManager();

    private static final Map<String, Session> SESSIONS =
            new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession(final String id) {
        final Session session =
                new Session(id, this);

        add(session);

        return session;
    }

    @Override
    public void add(final HttpSession session) {
        //SESSIONS

        if (!(session instanceof Session concreteSession)) {
            throw new IllegalArgumentException(
                    "SessionManager는 오직 Session만 관리한다."
            );
        }

        SESSIONS.put(
                concreteSession.getId(),
                concreteSession
        );
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }

        final Session session = SESSIONS.get(id);

        if (session != null) {
            session.access();
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