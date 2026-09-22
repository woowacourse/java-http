package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class SessionManager implements Manager {
    public static final String SESSION_ID = "JSESSIONID";

    private final Map<String, Session> sessionMap;
    private final LongSupplier currentTimeMillis;

    public SessionManager() {
        this(System::currentTimeMillis);
    }

    public SessionManager(LongSupplier currentTimeMillis) {
        this.sessionMap = new ConcurrentHashMap<>();
        this.currentTimeMillis = currentTimeMillis;
    }

    @Override
    public HttpSession createSession() {
        Session session = Session.create(currentTimeMillis.getAsLong());
        sessionMap.put(session.getId(), session);
        return session;
    }

    @Override
    public HttpSession findSession(String id) {
        Session session = sessionMap.get(id);
        if (session == null) {
            return null;
        }
        long now = currentTimeMillis.getAsLong();
        if (session.isExpired(now)) {
            sessionMap.remove(id, session);
            return null;
        }
        session.access(now);
        return session;
    }

    @Override
    public void remove(HttpSession session) {
        sessionMap.remove(session.getId());
    }
}
