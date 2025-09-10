package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {


    // 세션 풀
    private static final Map<String, HttpSessionImpl> SESSIONS = new ConcurrentHashMap<>();

    // 싱글턴
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * 세션 생성
     */
    public HttpSessionImpl createSession(ServletContext servletContext) {
        HttpSessionImpl session = new HttpSessionImpl(servletContext);
        add(session);
        return session;
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        if (id == null) return null;

        HttpSessionImpl session = SESSIONS.get(id);
        if (session == null) return null;

        if (session.isExpired()) {
            remove(session);
            return null;
        }

        session.access();
        return session;
    }

    @Override
    public void add(HttpSession session) {
        if (session instanceof HttpSessionImpl s) {
            SESSIONS.put(s.getId(), s);
        }
    }

    @Override
    public void remove(HttpSession session) {
        if (session instanceof HttpSessionImpl s) {
            s.invalidate();
            SESSIONS.remove(s.getId());
        }
    }
}
